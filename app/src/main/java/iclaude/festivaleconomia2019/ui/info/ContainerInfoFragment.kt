package iclaude.festivaleconomia2019.ui.info


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.info.about.AboutFragment
import iclaude.festivaleconomia2019.ui.info.faq.FAQFragment
import iclaude.festivaleconomia2019.ui.info.info.InfoFragment
import iclaude.festivaleconomia2019.ui.info.preferences.SettingsFragment
import kotlinx.android.synthetic.main.fragment_info_container.*
import kotlinx.android.synthetic.main.fragment_info_container_appbar.*

class ContainerInfoFragment : Fragment() {

    /* This is a super-hacky solution to have a proper exit animation when a Fragment contains other Fragments.
     * Basically, we get a picture of the screen to simulate that children Fragments are still present, even if
     * their view is no longer there. See methods onPause and onDestroyView. */
    var b: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = InfoAdapter(childFragmentManager)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onPause() {
        super.onPause()
        b = loadBitmapFromView(view!!)
    }

    /* This is a super-hacky solution to have a proper exit animation when a Fragment contains other Fragments.
     * Basically, we get a picture of the screen to simulate that children Fragments are still present, even if
     * their view is no longer there. See methods onPause and onDestroyView. */
    override fun onDestroyView() {
        val bd = BitmapDrawable(resources, b)
        view!!.background = bd
        b = null

        super.onDestroyView()
    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(
            v.width,
            v.height, Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(
            0, 0, v.width,
            v.height
        )
        v.draw(c)
        return b
    }

    // ViewPager adapter.
    inner class InfoAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = 4

        override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> InfoFragment.newInstance()
                1 -> FAQFragment.newInstance()
                2 -> AboutFragment.newInstance()
                else -> SettingsFragment()
            }

        override fun getPageTitle(position: Int): CharSequence =
            when (position) {
                0 -> getString(R.string.info_event)
                1 -> getString(R.string.info_faq)
                2 -> getString(R.string.info_about)
                else -> getString(R.string.info_settings)
            }
    }

}
