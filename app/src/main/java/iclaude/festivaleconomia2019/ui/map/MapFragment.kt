package iclaude.festivaleconomia2019.ui.map

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mViewModel: MapViewModel
    private lateinit var mMapView: MapView
    private lateinit var binding: FragmentMapBinding
    private lateinit var ivExpandIcon: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val root = inflater.inflate(R.layout.fragment_map, container, false)

        mViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        binding = FragmentMapBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@MapFragment)
            viewModel = this@MapFragment.mViewModel
        }

        // initialize MapView
        mMapView = binding.mainContent.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapFragment)
        }

        // show directions to a location when the user wants to
        mViewModel.directionsEvent.observe(this, Observer {
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
                    //To change body of created functions use File | Settings | File Templates.
                }

                override fun onStateChanged(p0: View, state: Int) {
                    if (state == STATE_COLLAPSED || state == STATE_EXPANDED) {
                        mViewModel.updateBottomSheetState(state)

                        (ivExpandIcon.drawable as AnimatedVectorDrawable).start()
                    }
                }
            })
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onMapReady(gMap: GoogleMap?) {
        mViewModel.mRepository.eventDataLive.observe(this, Observer {
            mViewModel.loadMap(it.locations)
        })

        gMap?.apply {
            setOnMarkerClickListener {
                mViewModel.zoomToMarker(it)
                true
            }
            setOnMapClickListener { mViewModel.onMapClick() }
        }

    }

}
