package iclaude.festivaleconomia2019.ui.info.about

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import kotlinx.android.synthetic.main.fragment_info_about.*


class AboutFragment : Fragment(), AboutView {

    private lateinit var presenter: AboutPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = AboutPresenter(this, context!!)

        return inflater.inflate(R.layout.fragment_info_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.displayData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }


    /**
     * Implementation of AboutView interface used to display formatted data
     * (SpannableStrings) in this Fragment.
     */

    override fun showAuthor(spannableString: SpannableString) {
        tvAuthor.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showDescription(spannableString: SpannableString) {
        tvDesc.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showGitHubText(spannableString: SpannableString) {
        tvGithub.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showNerdsDetails(spannableString: SpannableString) {
        tvNerd.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showPrivacyPolicy(spannableString: SpannableString) {
        tvPrivacy.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
