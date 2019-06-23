package iclaude.festivaleconomia2019.ui.map

import android.app.Activity
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.MapmarkerBinding
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.ui.utils.isDarkThemeActive

class CustomMarkerInfoWindow(val activity: Activity, val mapViewModel: MapViewModel) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker?): View? {
        if (activity.isDarkThemeActive) return null

        val binding =
            DataBindingUtil.inflate<MapmarkerBinding>(activity.layoutInflater, R.layout.mapmarker, null, true)
                .apply {
                    viewModel = mapViewModel
                    location = marker?.tag as Location
                    executePendingBindings()
                }

        return binding.root
    }

    override fun getInfoWindow(marker: Marker?): View? {
        if (!activity.isDarkThemeActive) return null

        val binding =
            DataBindingUtil.inflate<MapmarkerBinding>(activity.layoutInflater, R.layout.mapmarker, null, true)
                .apply {
                    viewModel = mapViewModel
                    location = marker?.tag as Location
                    executePendingBindings()
                }

        return binding.root
    }
}