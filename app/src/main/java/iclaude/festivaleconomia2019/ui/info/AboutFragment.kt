package iclaude.festivaleconomia2019.ui.info

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.MyClickableSpan
import kotlinx.android.synthetic.main.fragment_info_about.*


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_info_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createSpans()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }

    // Create SpannableStrings for this page: clickable links and other formattings.
    private fun createSpans() {
        // Link to developer's LinkedIn page.
        val spanAuthor = SpannableString(tvAuthor.text).apply {
            setSpan(
                MyClickableSpan(context!!, getString(R.string.info_about_linkedInUrl)), 3, length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvAuthor.apply {
            text = spanAuthor
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }

        // Link to the app's GitHub repository.
        val subStrToSpanGitHub = getString(R.string.info_about_githubrepo)
        val completeStrGitHub = getString(R.string.info_about_github, subStrToSpanGitHub)
        val spanGithub = SpannableString(completeStrGitHub).apply {
            setSpan(
                MyClickableSpan(context!!, getString(R.string.info_about_githubUrl)),
                indexOf(subStrToSpanGitHub),
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvGithub.apply {
            text = spanGithub
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }

        // Link to Google IO 2018 app's GitHub repository.
        val subStrToSpanGoogleApp = "Google IO 2018"
        val spanNerd = SpannableString(tvNerd.text).apply {
            setSpan(
                MyClickableSpan(context!!, getString(R.string.info_about_googleIOappUrl)),
                indexOf(subStrToSpanGoogleApp),
                indexOf(subStrToSpanGoogleApp) + 14,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvNerd.apply {
            text = spanNerd
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }
    }
}
