package iclaude.festivaleconomia2019.ui.map

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.utils.Event
import javax.inject.Inject

class MapViewModel : ViewModel() {

    // set the state of BottomSheet with location's info
    private val _bottomSheetStateEvent = MutableLiveData<Event<Int>>()
    val bottomSheetStateEvent: LiveData<Event<Int>>
        get() = _bottomSheetStateEvent

    init {
        App.component.inject(this)
        loadDataFromRepo()
        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_HIDDEN)
    }

    // Initialization.
    private fun loadDataFromRepo() {
        if (!repository.dataLoaded) repository.loadEventDataFromJSONFile()
    }

    @Inject
    lateinit var repository: EventDataRepository

    val eventDataFromRepoLive = Transformations.switchMap(repository.eventDataLive) {
        MutableLiveData<EventData>().apply {
            value = it
        }
    }

    // Location id to navigate to.
    var locationId =
        -1 // -1 if MapFragment is opened wihout a specific location to navigate to, or locationId otherwise (if opened from SessionInfoFragment with the id of the location to display).

    // center the map on a specific point with animation
    private val _mapCenterEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapCenterEvent: LiveData<Event<CameraUpdate>>
        get() = _mapCenterEvent

    // center the map on a specific point without animation (after rotating device)
    private val _mapRotateEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapRotateEvent: LiveData<Event<CameraUpdate>>
        get() = _mapRotateEvent

    // add markers to the map
    private val _mapMarkersEvent = MutableLiveData<Event<LocationsAndSelectedLocation>>()
    val mapMarkersEvent: LiveData<Event<LocationsAndSelectedLocation>>
        get() = _mapMarkersEvent

    // set marker's name and description in the bottom sheet
    private val _markerInfoEvent = MutableLiveData<Event<Location>>()
    val markerInfoEvent: LiveData<Event<Location>>
        get() = _markerInfoEvent

    // list of sessions held at the selected location
    private val _sessionListEvent = MutableLiveData<Event<List<Session>?>>()
    val sessionListEvent: LiveData<Event<List<Session>?>>
        get() = _sessionListEvent

    // the user clicks on map icon to get directions to the selected location
    private val _directionsEvent = MutableLiveData<Event<Location>>()
    val directionsEvent: LiveData<Event<Location>>
        get() = _directionsEvent

    private var curLocation: Location? = null
    private var mapLoaded = false


    // Load markers and center the map.
    fun loadMap(locations: List<Location>) {
        if (locationId != -1) { // zoom to a specific location with animation
            zoomToMarker(location = locations[locationId])
            locationId = -1
        } else if (mapLoaded) {
            _mapRotateEvent.value = Event(getCameraUpdate(locations))
        } else {
            _mapCenterEvent.value = Event(getCameraUpdate(locations))
        }
        _mapMarkersEvent.value = Event(LocationsAndSelectedLocation(locations, curLocation))

        mapLoaded = true
    }

    private fun getCameraUpdate(locations: List<Location>): CameraUpdate {
        // a marker is already selected: zoom to that marker
        if (curLocation != null) {
            return CameraUpdateFactory.newLatLngZoom(LatLng(curLocation!!.lat, curLocation!!.lng), 17f)
        }

        // no marker is selected: zoom to the center
        val latLngBounds = LatLngBounds.builder().run {
            locations.forEach {
                include(LatLng(it.lat, it.lng))
            }
            build()
        }
        return CameraUpdateFactory.newLatLngBounds(latLngBounds, 120)

    }

    private fun getCameraUpdateForDestination(location: Location): CameraUpdate {
        return CameraUpdateFactory.newLatLngZoom(LatLng(location.lat, location.lng), 17f)
    }

    /* When clicking on a marker: zoom to marker, center the map and display bottom sheet (collapsed).
       When navigating from SessionInfoFragment marker is null and a location is provided instead:
       in this case navigate to the selected location showing info in the BottomSheet but without
       displaying the info window or select a specific Marker. */
    fun zoomToMarker(marker: Marker? = null, location: Location? = null) {
        if (marker == null && location == null) return

        val loc = marker?.tag as? Location ?: location
        curLocation = loc!! // at this point loc must not be null

        val cameraPosition = CameraPosition.Builder().run {
            this.target(LatLng(loc.lat, loc.lng))
            this.zoom(17f)
            build()
        }
        _mapCenterEvent.value = Event(CameraUpdateFactory.newCameraPosition(cameraPosition))
        _markerInfoEvent.value = Event(loc)
        _sessionListEvent.value = Event(repository.eventDataLive.value?.sessions?.filter {
            it.location == loc.id
        })
        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_COLLAPSED)
    }

    // When clicking on the map: hide bottom sheet.
    fun onMapClick() {
        _bottomSheetStateEvent.value = Event(BottomSheetBehavior.STATE_HIDDEN)
        curLocation = null
    }

    // Show directions to a location.
    fun showRoute(view: View) {
        curLocation ?: return
        _directionsEvent.postValue(Event(curLocation!!))
    }

    // Click on bottom sheet title: expand or collapse.
    fun onBottomSheetExpandIconClick(view: View) {
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

    // User clicks to session in the list of sessions to see more details.

    private val _goToSessionEvent = MutableLiveData<Event<String>>()
    val goToSessionEvent: LiveData<Event<String>>
        get() = _goToSessionEvent

    fun goToSession(sessionId: String) {
        _goToSessionEvent.value = Event(sessionId)
    }

    override fun onCleared() {
        super.onCleared()
        curLocation = null
        repository.cancelLoadingData()
    }
}

class LocationsAndSelectedLocation(val locations: List<Location>, val selectedLocation: Location?)