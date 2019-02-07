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
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionsContainerBinding
import kotlinx.android.synthetic.main.fragment_session_container.*
import kotlinx.android.synthetic.main.fragment_session_container_appbar.*


class SessionContainerFragment : Fragment() {
    private val TAG = "VIEW_MODEL"

    private lateinit var viewModel: SessionContainerViewModel
    private lateinit var binding: FragmentSessionsContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(SessionContainerViewModel::class.java)
        binding = FragmentSessionsContainerBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionContainerFragment.viewModel
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.setupWithViewPager(viewPager)

        // load data from repository
        viewModel.repository.eventDataLive.observe(this, Observer {
            viewModel.dataLoadedObs.set(true)
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
                in 0 until numOfDays -> {
                    val day = dayLabels[position].date
                    SessionListFragment.newInstance(
                        day!!.year,
                        day.monthValue,
                        day.dayOfMonth,
                        day.hour,
                        day.minute,
                        day.second,
                        day.zone.toString()
                    )
                }
                else -> AgendaFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return dayLabels[position].label
        }
    }
}
