package iclaude.festivaleconomia2019.ui.info.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString
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
        val authorStr = tvAuthor.text.toString()
        val spanAuthor = buildSpannableString {
            string = authorStr

            span {
                start = 3
                end = authorStr.length
                clickableSpan {
                    context = getContext()
                    url = getString(R.string.info_about_linkedInUrl)

                }
            }
        }
        tvAuthor.apply {
            text = spanAuthor
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }

        // App's description.
        val unofficialAppStr = getString(R.string.info_about_desc_unofficial_app)
        val economicsFestivalStr = getString(R.string.info_about_desc_economics_festival)
        val description = getString(R.string.info_about_desc, unofficialAppStr, economicsFestivalStr)
        val spanDesc = buildSpannableString {
            string = description

            span {
                start = description.indexOf(unofficialAppStr)
                end = description.indexOf(unofficialAppStr) + unofficialAppStr.length

                bold()
                increaseSizeBy(1.2f)
                underline()
            }

            span {
                start = description.indexOf(economicsFestivalStr)
                end = description.indexOf(economicsFestivalStr) + economicsFestivalStr.length
                bold()
                increaseSizeBy(1.2f)
                clickableSpan {
                    context = getContext()
                    url = getString(R.string.official_site_url)
                }
            }
        }
        tvDesc.apply {
            text = spanDesc
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }

        // GitHub repository info.
        val subStrToSpanGitHub = getString(R.string.info_about_githubrepo)
        val completeStrGitHub = getString(R.string.info_about_github, subStrToSpanGitHub)
        val spanGitHub = buildSpannableString {
            string = getString(R.string.info_about_github, subStrToSpanGitHub)

            span {
                start = completeStrGitHub.indexOf(subStrToSpanGitHub)
                end = completeStrGitHub.length
                clickableSpan {
                    context = getContext()
                    url = getString(R.string.info_about_githubUrl)
                }
            }

            span {
                start = completeStrGitHub.indexOf("open source")
                end = completeStrGitHub.indexOf("open source") + 11
                bold()
                increaseSizeBy(1.2f)
            }
        }
        tvGithub.apply {
            text = spanGitHub
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }

        // Nerd's details.
        val nerdsDetails = tvNerd.text.toString()
        val subStrToSpanGoogleApp = "Google IO 2018"
        val spanNerd = buildSpannableString {
            string = nerdsDetails

            span {
                start = nerdsDetails.indexOf(subStrToSpanGoogleApp)
                end = nerdsDetails.indexOf(subStrToSpanGoogleApp) + 14

                clickableSpan {
                    context = getContext()
                    url = getString(R.string.info_about_googleIOappUrl)
                }
            }

            span {
                start = nerdsDetails.indexOf("MVVM pattern")
                end = nerdsDetails.indexOf("MVVM pattern") + 12
                bold()
                italic()
                increaseSizeBy(1.2f)
            }
        }
        tvNerd.apply {
            text = spanNerd
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = true
            isClickable = true
        }
    }
}
