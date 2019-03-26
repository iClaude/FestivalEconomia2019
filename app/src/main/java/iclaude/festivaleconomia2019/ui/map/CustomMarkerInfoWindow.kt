package iclaude.festivaleconomia2019.ui.map

import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomMarkerInfoWindow(val viewModel: MapViewModel) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}