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

    // center the map on a specific point with animation
    private val _mapCenterEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapCenterEvent: LiveData<Event<CameraUpdate>>
        get() = _mapCenterEvent

    // center the map on a specific point without animation (after rotating device)
    private val _mapRotateEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapRotateEvent: LiveData<Event<CameraUpdate>>
        get() = _mapRotateEvent

    // add markers to the map
    private val _mapMarkersEvent = MutableLiveData<Event<LocationsAndSelectedMarker>>()
    val mapMarkersEvent: LiveData<Event<LocationsAndSelectedMarker>>
        get() = _mapMarkersEvent

    // set marker's name and description in the bottom sheet
    private val _markerInfoEvent = MutableLiveData<Event<Location>>()
    val markerInfoEvent: LiveData<Event<Location>>
        get() = _markerInfoEvent

    // the user clicks on map icon to get directions to the selected location
    private val _directionsEvent = MutableLiveData<Event<Location>>()
    val directionsEvent: LiveData<Event<Location>>
        get() = _directionsEvent

    private var curMarker: Marker? = null
    private var selectedMarkerId: String = "xx"
    private var mapLoaded = false


    // Load markers and center the map.
    fun loadMap(locations: List<Location>) {
        if (mapLoaded) {
            _mapRotateEvent.value = Event(getCameraUpdate(locations))
        } else {
            _mapCenterEvent.value = Event(getCameraUpdate(locations))
        }
        _mapMarkersEvent.value = Event(LocationsAndSelectedMarker(locations, selectedMarkerId))

        mapLoaded = true
    }

    private fun getCameraUpdate(locations: List<Location>): CameraUpdate {
        // a marker is already selected: zoom to that marker
        if (curMarker != null) {
            val loc = curMarker?.tag as Location
            return CameraUpdateFactory.newLatLngZoom(LatLng(loc.lat, loc.lng), 17f)
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

    // When clicking on a marker: zoom to marker, center the map and display bottom sheet (collapsed).
    fun zoomToMarker(marker: Marker) {
        val loc = marker.tag as Location
        selectedMarkerId = loc.id
        curMarker = marker

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
        curMarker = null
        selectedMarkerId = "xx"
    }

    // Show directions to a location.
    fun showRoute(view: View) {
        _directionsEvent.postValue(Event(curMarker?.tag as Location))
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

    override fun onCleared() {
        super.onCleared()
        selectedMarkerId = "xx"
        repository.cancelLoadingData()
    }
}

class LocationsAndSelectedMarker(val locations: List<Location>, val selectedMarkerId: String)