package iclaude.festivaleconomia2019.ui.info


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import iclaude.festivaleconomia2019.R
import kotlinx.android.synthetic.main.fragment_info_container.*
import kotlinx.android.synthetic.main.fragment_info_container_appbar.*

class ContainerInfoFragment : Fragment() {

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

    // ViewPager adapter.
    inner class InfoAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = 4

        override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> InfoFragment.newInstance()
                1 -> FAQFragment.newInstance()
                2 -> AboutFragment.newInstance()
                else -> SettingsFragment.newInstance()
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