package iclaude.festivaleconomia2019.ui.info.preferences

import android.os.Bundle
import iclaude.festivaleconomia2019.R


class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
