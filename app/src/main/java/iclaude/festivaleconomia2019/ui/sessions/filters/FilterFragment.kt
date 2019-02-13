package iclaude.festivaleconomia2019.ui.sessions.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iclaude.festivaleconomia2019.databinding.FragmentSessionListFiltersheetBinding
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel

class FilterFragment() : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSessionListFiltersheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSessionListFiltersheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setViewModel(viewModel: SessionListViewModel) {
        //binding.viewModel = viewModel
    }
}