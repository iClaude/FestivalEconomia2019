package iclaude.festivaleconomia2019.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import iclaude.festivaleconomia2019.databinding.FragmentMapBinding


const val LOCATION_PERMISSION = 99

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var binding: FragmentMapBinding
    private lateinit var ivExpandIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@MapFragment
            viewModel = this@MapFragment.viewModel
        }

        // initialize MapView
        mapView = binding.mainContent.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapFragment)
        }

        // show directions to a location when the user wants to
        viewModel.directionsEvent.observe(this, Observer {
            val uri = Uri.parse("google.navigation:q=${it.lat},${it.lng}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                `package` = "com.google.android.apps.maps"
            }
            if (intent.resolveActivity(context?.packageManager) != null) {
                startActivity(intent)
            }
        })

        ivExpandIcon = binding.bottomSheet.expandIcon
        // update bottom sheet state after drag events
        BottomSheetBehavior.from(binding.bottomSheet.bottomSheet).apply {
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {

                }

                override fun onStateChanged(p0: View, state: Int) {
                    if (state == STATE_COLLAPSED || state == STATE_EXPANDED) {
                        viewModel.updateBottomSheetState(state)
                    }
                }
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(gMap: GoogleMap?) {
        googleMap = gMap

        viewModel.repository.eventDataLive.observe(this, Observer {
            viewModel.loadMap(it.locations)
        })

        gMap?.apply {
            setOnMarkerClickListener {
                viewModel.zoomToMarker(it)
                it.showInfoWindow()
                true
            }
            setOnMapClickListener { viewModel.onMapClick() }
        }

        // request for displaying user's location
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showUserPosition()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showUserPosition()
                }
            }
            else -> {
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showUserPosition() {
        googleMap?.isMyLocationEnabled = true

    }
}
