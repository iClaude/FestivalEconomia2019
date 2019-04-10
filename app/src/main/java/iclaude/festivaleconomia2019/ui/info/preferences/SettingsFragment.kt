package iclaude.festivaleconomia2019.ui.info.preferences

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder


class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // If user disables notifications cancel all requests in the WorkManager.
        findPreference<SwitchPreferenceCompat>(getString(R.string.info_pref_notifications_key))?.let {
            it.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
                override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                    val notificationsEnabled = newValue as Boolean
                    if (!notificationsEnabled)
                        WorkRequestBuilder.deleteAllRequests()
                    return true
                }
            }
        }
    }
}
