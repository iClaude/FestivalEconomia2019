package iclaude.festivaleconomia2019.ui.info.preferences

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder
import iclaude.festivaleconomia2019.utils.getQuantityString


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

        // Notifications: hours in advance before the event.
        findPreference<SeekBarPreference>(getString(R.string.info_pref_notifications_hours_key))?.let { seekBarPreference ->
            // Initial value.
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val hoursInAdvance = sharedPref.getInt(getString(R.string.info_pref_notifications_hours_key), 1)
            context?.getQuantityString(R.plurals.info_pref_notifications_hours_summary, hoursInAdvance, hoursInAdvance)
                ?.let {
                    seekBarPreference.summary = it
                }

            // Subsequent changes.
            seekBarPreference.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
                override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                    val hours = newValue as Int
                    context?.getQuantityString(R.plurals.info_pref_notifications_hours_summary, hours, hours)?.let {
                        preference?.summary = it
                    }

                    return true
                }
            }
        }

    }
}
