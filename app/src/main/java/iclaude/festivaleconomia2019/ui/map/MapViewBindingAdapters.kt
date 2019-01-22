package iclaude.festivaleconomia2019.ui.map

import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.ui.utils.Event


private const val TAG = "MapViewModel"


/**
 * Sets the center of the map's camera. Call this every time the user selects a marker.
 */
@BindingAdapter("app:mapCenter")
fun mapCenter(mapView: MapView, event: Event<CameraUpdate>?) {
    val update = event?.getContentIfNotHandled() ?: return
    mapView.getMapAsync {
        it.animateCamera(update)
    }
}

/**
 * Adds markers to the map.
 */
@BindingAdapter("app:mapMarkers")
fun mapMarkers(mapView: MapView, event: Event<List<Location>>?) {
    val locations = event?.getContentIfNotHandled() ?: return

    mapView.getMapAsync { gMap ->
        locations.forEach { loc ->
            gMap.addMarker(
                MarkerOptions()
                    .position(LatLng(loc.lat, loc.lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            ).apply {
                title = loc.name
                tag = loc
            }


        }
    }
}