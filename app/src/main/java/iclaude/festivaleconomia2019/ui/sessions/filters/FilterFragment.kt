package iclaude.festivaleconomia2019.ui.sessions.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import iclaude.festivaleconomia2019.databinding.FragmentSessionListFiltersheetBinding
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel
import kotlinx.android.synthetic.main.fragment_session_list_filtersheet.*

class FilterFragment() : BottomSheetDialogFragment() {
    private lateinit var viewModel: SessionListViewModel
    private lateinit var binding: FragmentSessionListFiltersheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSessionListFiltersheetBinding.inflate(inflater, container, false).apply {
            viewModel = this@FilterFragment.viewModel
        }

        val cStarred = binding.chipStarred
        cStarred.setOnCheckedChangeListener { compoundButton, isChecked ->
            val filter = viewModel.filterSelected.value
            filter?.starred = isChecked
            viewModel.filterSelected.value = filter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bReset.setOnClickListener {
            viewModel.filterSelected.value = Filter()

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
    }
}