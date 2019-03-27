package iclaude.festivaleconomia2019.ui.map

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.ui.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "ICON_EXPAND"
private val viewModelJob = Job()
private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)
private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)


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
 * Markers.
 */

// Add markers to map.
@BindingAdapter("app:mapMarkers")
fun mapMarkers(mapView: MapView, event: Event<LocationsAndSelectedLocation>?) {
    val eventContent = event?.getContentIfNotHandled() ?: return

    mapView.getMapAsync { gMap ->
        defaultScope.launch {
            val locations = mutableListOf<Location>()
            val markers = eventContent.locations.map { loc ->
                locations.add(loc)
                MarkerOptions()
                    .position(LatLng(loc.lat, loc.lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            }

            mainScope.launch {
                markers.forEachIndexed { index, markerOptions ->
                    gMap.addMarker(markerOptions).apply {
                        title = locations[index].name
                        tag = locations[index]
                        if (locations[index] == eventContent.selectedLocation) showInfoWindow()
                    }
                }
            }
        }
    }
}

// Marker's InfoWindow: name, truncated if lenght > 35 characters.
@BindingAdapter("app:textWithMaxLength")
fun textWithMaxLenght(textView: TextView, str: String) {
    textView.text = if (str.length <= 35) str else str.take(35) + "..."
}

// Marker's InfoWindow: number of events held at this location.
@BindingAdapter("app:viewModel", "app:location", requireAll = true)
fun showNumOfEventsOnInfoWindow(textView: TextView, viewModel: MapViewModel, location: Location) {
    val sessions = viewModel.eventDataFromRepoLive.value?.sessions ?: return

    val context = textView.context
    val numOfEvents = sessions.filter { it.location == location.id }.size

    textView.apply {
        text = context.resources.getQuantityString(R.plurals.map_events_here, numOfEvents, numOfEvents)
        if (numOfEvents == 0)
            setTextColor(ContextCompat.getColor(context, R.color.link))
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
 * Sets marker's data (title, description, lat/lng, session list) in the bottom sheet.
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

// Title for programmed events visibility.
@BindingAdapter("app:sessionsForLocation")
fun programmedSessionsTitle(textView: TextView, event: Event<List<Session>?>?) {
    val sessions = event?.getContent() ?: return

    textView.visibility = if (sessions.isNotEmpty()) View.VISIBLE else View.GONE
}

// List of programmed events.
@BindingAdapter("app:sessionsForLocation", "app:viewModel", requireAll = true)
fun addSessionsForLocation(recyclerView: RecyclerView, event: Event<List<Session>?>?, viewModel: MapViewModel) {
    val sessions = event?.getContent() ?: return

    recyclerView.adapter = SessionListAdapter(viewModel).apply { submitList(sessions) }
    viewModel.updateSessionListWithStarredSessions(sessions)
}

// Update list of programmed events with starred events.
@BindingAdapter("app:starredSessions")
fun updateSessionListWithStarredSessions(recyclerView: RecyclerView, event: Event<List<Session>?>?) {
    val sessions = event?.getContent() ?: return
    recyclerView.adapter ?: return

    (recyclerView.adapter as SessionListAdapter).apply {
        submitList(sessions)
        notifyDataSetChanged()
    }
}
