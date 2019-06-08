package iclaude.festivaleconomia2019.ui.info.preferences

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.ListPreference
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

        findPreference<ListPreference>(getString(R.string.info_pref_theme_key))?.let {
            it.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
                override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                    val valueChosen = (newValue as String).toInt()
                    setNightMode(valueChosen)

                    return true
                }
            }
        }

    }
}

fun setNightMode(valueChosen: Int) {
    require(valueChosen in 0..2) { "Theme value $valueChosen invalid: should be in 0..2 interval" }

    val themeSetting = when (valueChosen) {
        0 -> MODE_NIGHT_NO
        1 -> MODE_NIGHT_YES
        2 -> if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) MODE_NIGHT_FOLLOW_SYSTEM
        else MODE_NIGHT_AUTO_BATTERY
        else -> MODE_NIGHT_NO
    }
    setDefaultNightMode(themeSetting)
}