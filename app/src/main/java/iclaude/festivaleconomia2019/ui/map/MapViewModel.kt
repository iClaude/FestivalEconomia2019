package iclaude.festivaleconomia2019.ui.map

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.utils.Event
import iclaude.festivaleconomia2019.ui.utils.SingleLiveEvent
import javax.inject.Inject

class MapViewModel : ViewModel() {
    private val TAG = this.javaClass.simpleName


    // center the map on a specific point
    private val _mapCenterEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapCenterEvent: LiveData<Event<CameraUpdate>>
        get() = _mapCenterEvent

    // add markers to the map
    private val _mapMarkersEvent = MutableLiveData<Event<List<Location>>>()
    val mapMarkersEvent: LiveData<Event<List<Location>>>
        get() = _mapMarkersEvent

    // set the state of BottomSheet with location's info
    private val _bottomSheetStateEvent = MutableLiveData<Event<Int>>()
    val bottomSheetStateEvent: LiveData<Event<Int>>
        get() = _bottomSheetStateEvent

    // set marker's name and description in the bottom sheet
    private val _markerInfoEvent = MutableLiveData<Event<Location>>()
    val markerInfoEvent: LiveData<Event<Location>>
        get() = _markerInfoEvent

    // the user clicks on map icon to get directions to the selected location
    val directionsEvent = SingleLiveEvent<Location>()

    init {
        App.component.inject(this)

        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_HIDDEN)
    }

    @Inject
    lateinit var mRepository: EventDataRepository

    private var curLocation: Location? = null


    // Load markers and center the map.
    fun loadMap(locations: List<Location>) {
        val latLngBounds = LatLngBounds.builder().run {
            locations.forEach {
                include(LatLng(it.lat, it.lng))
            }
            build()
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 120)
        _mapCenterEvent.value = Event(cameraUpdate)
        _mapMarkersEvent.value = Event(locations)

    }

    // When clicking on a marker: zoom to marker, center the map and display bottom sheet (collapsed).
    fun zoomToMarker(marker: Marker) {
        val loc = marker.tag as Location
        curLocation = loc

        val cameraPosition = CameraPosition.Builder().run {
            this.target(LatLng(loc.lat, loc.lng))
            this.zoom(17f)
            build()
        }
        _mapCenterEvent.value = Event(CameraUpdateFactory.newCameraPosition(cameraPosition))
        _markerInfoEvent.value = Event(loc)
        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_COLLAPSED)
    }

    // When clicking on the map: hide bottom sheet.
    fun onMapClick() {
        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_HIDDEN)
    }

    // Show directions to a location.
    fun showRoute(view: View) {
        directionsEvent.postValue(curLocation)
    }

    // Click on bottom sheet title: expand or collapse.
    fun onBottomSheetTitleClick(view: View) {
        val state = (bottomSheetStateEvent.value as Event<Int>).peekContent()
        when (state) {
            BottomSheetBehavior.STATE_COLLAPSED -> _bottomSheetStateEvent.value =
                    Event(BottomSheetBehavior.STATE_EXPANDED)
            BottomSheetBehavior.STATE_EXPANDED -> _bottomSheetStateEvent.value =
                    Event(BottomSheetBehavior.STATE_COLLAPSED)
        }
        false
    }

    // Update the state of the bottom sheet after drag events.
    fun updateBottomSheetState(state: Int) {
        val curState = (bottomSheetStateEvent.value as Event<Int>).peekContent()
        if (state != curState) {
            _bottomSheetStateEvent.value = Event(state)
        }
    }
}