package iclaude.festivaleconomia2019.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import iclaude.festivaleconomia2019.databinding.FragmentMapBinding
import iclaude.festivaleconomia2019.model.data_classes.Location


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mViewModel: MapViewModel
    private lateinit var mMapView: MapView
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mLocations: List<Location>
    private lateinit var binding: FragmentMapBinding


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

        mMapView = binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapFragment)
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
        mGoogleMap = gMap

        mViewModel.mRepository.eventDataLive.observe(this, Observer {
            mViewModel.onMapReady(it.locations)
        })
    }


    private fun addMarker(location: Location, mMap: GoogleMap) {
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.lat, location.lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
            .title = location.name

    }
}
