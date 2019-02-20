package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.chip.Chip
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionContainerBinding
import iclaude.festivaleconomia2019.ui.sessions.filters.Filter
import kotlinx.android.synthetic.main.fragment_session_container.*
import kotlinx.android.synthetic.main.fragment_session_container_appbar.*
import kotlinx.android.synthetic.main.fragment_session_list_filtersheet.*
import kotlinx.android.synthetic.main.fragment_session_list_filtersheet.view.*


class SessionContainerFragment : Fragment() {

    private lateinit var viewModel: SessionListViewModel
    private lateinit var binding: FragmentSessionContainerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionContainerBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionContainerFragment.viewModel
        }

        binding.root.chipStarred.apply {
            setOnCheckedChangeListener { compoundButton, isChecked ->
                val filter = viewModel.filterSelected.value
                filter?.starred = isChecked
                viewModel.updateFilter(filter)
            }
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup ViewPager and TabLayout
        viewModel.repository.eventDataLive.observe(this, Observer { eventData ->
            with(viewModel) {
                dataLoadedObs.set(true)
                loadInfoList(eventData)
            }

            viewPager.adapter =
                SessionsAdapter(
                    childFragmentManager,
                    numberOfDays(context, eventData.sessions),
                    daysLabels(context, eventData.sessions).also {
                        it.add(DayLabel(label = getString(R.string.sessions_agenda)))
                    }
                )
        })
        tabs.setupWithViewPager(viewPager)

        // fab
        fabFilter.setOnClickListener {
            fragmentManager?.run {
                bottomSheetBehavior.state = STATE_EXPANDED
            }
        }

        // filter sheet
        bottomSheetBehavior = from(bottomSheet).apply {
            state = when (viewModel.hasAnyFiltersObs.get()) {
                true -> STATE_COLLAPSED
                else -> STATE_HIDDEN
            }
        }

        bReset.setOnClickListener {
            viewModel.updateFilter(Filter())

            cgTopics.forEach {
                val chip = it as Chip
                chip.isChecked = false
            }
            cgTypes.forEach {
                val chip = it as Chip
                chip.isChecked = false
            }
            chipStarred.isChecked = false
        }
        ibCollapse.setOnClickListener {
            bottomSheetBehavior.run {
                state = when (skipCollapsed) {
                    true -> STATE_HIDDEN
                    else -> STATE_COLLAPSED
                }
            }
        }
    }

    inner class SessionsAdapter(
        fm: FragmentManager,
        private var numDays: Int,
        private var tabsInfo: MutableList<DayLabel>
    ) : FragmentPagerAdapter(fm) {

        override fun getCount() = numDays + 1

        override fun getItem(position: Int): Fragment {
            return when (position) {
                in 0 until numDays -> {
                    SessionListFragment.newInstance(position)
                }
                else -> AgendaFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabsInfo[position].label
        }
    }
}
