package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionsContainerBinding


class ContainerSessionsFragment : Fragment() {
    private val TAG = "VIEW_MODEL"

    private lateinit var mViewModel: SessionsViewModel
    private lateinit var binding: FragmentSessionsContainerBinding
    private lateinit var viewPager: ViewPager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(activity!!).get(SessionsViewModel::class.java)
        binding = FragmentSessionsContainerBinding.inflate(inflater, container, false).apply {
            viewModel = this@ContainerSessionsFragment.mViewModel
        }
        viewPager = binding.viewPager

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs: TabLayout = view.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        // load data from repository
        mViewModel.mRepository.eventDataLive.observe(this, Observer {
            mViewModel.loadData(it)

            viewPager.adapter =
                SessionsAdapter(
                    childFragmentManager,
                    numberOfDays(context, it.sessions),
                    daysLabels(context, it.sessions)
                )
        })
    }

    inner class SessionsAdapter(
        fm: FragmentManager,
        private var numOfDays: Int,
        private var dayLabels: MutableList<DayLabel>
    ) : FragmentPagerAdapter(fm) {
        init {
            dayLabels.add(DayLabel(null, getString(R.string.sessions_agenda)))
        }

        override fun getCount() = numOfDays + 1

        override fun getItem(position: Int): Fragment {
            return when (position) {
                in 0 until numOfDays -> SessionsFragment.newInstance(dayLabels[position].date!!.toInstant().toEpochMilli())
                else -> AgendaFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return dayLabels[position].label
        }
    }
}
