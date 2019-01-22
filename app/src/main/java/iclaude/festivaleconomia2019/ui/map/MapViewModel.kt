package iclaude.festivaleconomia2019.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.utils.Event
import javax.inject.Inject

class MapViewModel : ViewModel() {
    private val TAG = "MapViewModel"

    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var mRepository: EventDataRepository


    private val _mapCenterEvent = MutableLiveData<Event<CameraUpdate>>()
    val mapCenterEvent: LiveData<Event<CameraUpdate>>
        get() = _mapCenterEvent

    private val _mapMarkersEvent = MutableLiveData<Event<List<Location>>>()
    val mapMarkersEvent: LiveData<Event<List<Location>>>
        get() = _mapMarkersEvent


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

    fun zoomToMarker(marker: Marker) {
        val cameraPosition = CameraPosition.Builder().run {
            val loc = marker.tag as Location
            this.target(LatLng(loc.lat, loc.lng))
            this.zoom(17f)
            build()
        }
        _mapCenterEvent.value = Event(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

}