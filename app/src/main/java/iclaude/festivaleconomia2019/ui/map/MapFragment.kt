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
import iclaude.festivaleconomia2019.databinding.FragmentMapBinding


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mViewModel: MapViewModel
    private lateinit var mMapView: MapView
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

        mMapView = binding.mainContent.mapView.apply {
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
