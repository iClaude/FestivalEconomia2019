package iclaude.festivaleconomia2019.ui.map

import android.util.Log

import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.MapView
import iclaude.festivaleconomia2019.ui.utils.Event


private const val TAG = "MapViewModel"


/**
 * Sets the center of the map's camera. Call this every time the user selects a marker.
 */
@BindingAdapter("app:mapCenter")
fun mapCenter(mapView: MapView, event: Event<CameraUpdate>?) {
    Log.d(TAG, "binding adapter")
    val update = event?.getContentIfNotHandled() ?: return
    mapView.getMapAsync {
        Log.d(TAG, "moving map")
        it.animateCamera(update)
    }
}

