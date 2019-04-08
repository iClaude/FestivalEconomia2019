package iclaude.festivaleconomia2019.ui.info.preferences

import android.annotation.SuppressLint
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView

/**
 * Remove extra padding on the left in the preference screen, as explained here:
 * https://stackoverflow.com/questions/18509369/android-how-to-remove-margin-padding-in-preference-screen
 */
abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    private fun setAllPreferencesToAvoidHavingExtraSpace(preference: Preference) {
        preference.isIconSpaceReserved = false
        if (preference is PreferenceGroup)
            for (i in 0 until preference.preferenceCount)
                setAllPreferencesToAvoidHavingExtraSpace(preference.getPreference(i))
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        if (preferenceScreen != null)
            setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen)
        super.setPreferenceScreen(preferenceScreen)

    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> =
        object : PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            override fun onPreferenceHierarchyChange(preference: Preference?) {
                if (preference != null)
                    setAllPreferencesToAvoidHavingExtraSpace(preference)
                super.onPreferenceHierarchyChange(preference)
            }
        }
}