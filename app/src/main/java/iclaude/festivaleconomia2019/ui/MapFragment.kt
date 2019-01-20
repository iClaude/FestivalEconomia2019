package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Location


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMapViewModel: MapViewModel
    private lateinit var mMapView: MapView
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mLocations: List<Location>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        mMapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        mMapView = root.findViewById<MapView>(R.id.mapView)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        return root
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

        mMapViewModel.eventDataLive.observe(this, Observer<List<Location>> {
            mLocations = it
            setupMap()
        })


    }

    private fun setupMap() {
        mGoogleMap?.let { mMap ->
            val latLngBounds = LatLngBounds.builder().run {
                mLocations.forEach { location ->
                    include(LatLng(location.lat, location.lng))
                    addMarker(location, mMap)
                }
                build()
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 15f))

        }
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
