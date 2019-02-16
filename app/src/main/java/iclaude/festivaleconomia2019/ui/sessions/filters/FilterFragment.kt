package iclaude.festivaleconomia2019.ui.sessions.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iclaude.festivaleconomia2019.databinding.FragmentSessionListFiltersheetBinding
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel

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
        return binding.root
    }

}