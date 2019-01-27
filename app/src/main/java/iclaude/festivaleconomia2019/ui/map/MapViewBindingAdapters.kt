package iclaude.festivaleconomia2019.ui.map

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.ui.utils.Event


private const val TAG = "ICON_EXPAND"

/**
 * Sets the center of the map's camera with animation. Call this every time the
 * user selects a marker.
 */
@BindingAdapter("app:mapCenter")
fun mapCenter(mapView: MapView, event: Event<CameraUpdate>?) {
    val update = event?.getContentIfNotHandled() ?: return
    mapView.getMapAsync {
        it.animateCamera(update)
    }
}

/**
 * Sets the center of the map's camera without animation. Call this every time the
 * user rotates the device.
 */
@BindingAdapter("app:mapRotate")
fun mapRotate(mapView: MapView, event: Event<CameraUpdate>?) {
    val update = event?.getContentIfNotHandled() ?: return
    mapView.getMapAsync {
        it.moveCamera(update)
    }
}

/**
 * Adds markers to the map.
 */
@BindingAdapter("app:mapMarkers")
fun mapMarkers(mapView: MapView, event: Event<LocationsAndSelectedMarker>?) {
    val eventContent = event?.getContentIfNotHandled() ?: return

    mapView.getMapAsync { gMap ->
        eventContent.locations.forEach { loc ->
            gMap.addMarker(
                MarkerOptions()
                    .position(LatLng(loc.lat, loc.lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            ).apply {
                title = loc.name
                tag = loc
                if (loc.id == eventContent.selectedMarkerId) showInfoWindow()
            }


        }
    }
}

/**
 * Sets the current state of the BottomSheet.
 */
@BindingAdapter("app:bottomSheetState")
fun bottomSheetState(view: View, event: Event<Int>?) {
    val state = event?.peekContent() ?: return
    BottomSheetBehavior.from(view).state = state

    if (!(state == BottomSheetBehavior.STATE_EXPANDED || state == BottomSheetBehavior.STATE_COLLAPSED)) return

    val ivIcon = view.findViewById<ImageView>(R.id.expand_icon)
    // animate icon rotation
    ivIcon.setImageResource(if (state == BottomSheetBehavior.STATE_EXPANDED) R.drawable.avd_arrow_up else R.drawable.avd_arrow_down)
    val drawable = ivIcon.drawable

    AnimatedVectorDrawableCompat.registerAnimationCallback(
        drawable,
        object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                ivIcon.setImageResource(if (state == BottomSheetBehavior.STATE_EXPANDED) R.drawable.avd_arrow_down else R.drawable.avd_arrow_up)
            }
        })
    (drawable as Animatable).start()
}

/**
 * Sets marker's data (title, description, lat/lng) in the bottom sheet.
 */
@BindingAdapter("app:markerTitle")
fun markerTitle(textView: TextView, event: Event<Location>?) {
    val location = event?.getContent() ?: return
    textView.text = location.name
}

@BindingAdapter("app:markerDescription")
fun markerDescription(textView: TextView, event: Event<Location>?) {
    val location = event?.getContent() ?: return
    textView.text = location.description
}

@BindingAdapter("app:markerTag")
fun markerTag(view: View, event: Event<Location>?) {
    val location = event?.getContent() ?: return
    view.tag = location
}