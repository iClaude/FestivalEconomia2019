package iclaude.festivaleconomia2019.ui.details.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R

class OrganizerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val idOrganizer = OrganizerFragmentArgs.fromBundle(arguments!!).organizerId

        return inflater.inflate(R.layout.fragment_organizer, container, false)
    }

}
