package iclaude.festivaleconomia2019.ui.info.about

import android.content.Context
import android.text.SpannableString
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString

/**
 * Classes, implementing the MVP pattern, used to provide formatted Strings to
 * AboutFragment to display.
 */

interface AboutView {
    fun showAuthor(spannableString: SpannableString)
    fun showDescription(spannableString: SpannableString)
    fun showGitHubText(spannableString: SpannableString)
    fun showNerdsDetails(spannableString: SpannableString)
}

class AboutPresenter(private var aboutView: AboutView, private var mContext: Context) {

    // Display all the formatted data in the associated View.
    fun displayData() {
        aboutView.showAuthor(createAuthorText())
        aboutView.showDescription(createDescriptionText())
        aboutView.showGitHubText(createGitHubText())
        aboutView.showNerdsDetails(createNerdsDetailsText())
    }

    /**
     * Create SpannableStrings with desired formats.
     */

    // Link to developer's LinkedIn page.
    private fun createAuthorText(): SpannableString {
        val authorStr = mContext.getString(R.string.info_about_author)
        return buildSpannableString {
            string = authorStr

            span {
                start = 3
                end = authorStr.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.info_about_linkedInUrl)

                }
            }
        }
    }

    // App's description.
    private fun createDescriptionText(): SpannableString {
        val unofficialAppStr = mContext.getString(R.string.info_about_desc_unofficial_app)
        val economicsFestivalStr = mContext.getString(R.string.info_about_desc_economics_festival)
        val description = mContext.getString(R.string.info_about_desc, unofficialAppStr, economicsFestivalStr)
        return buildSpannableString {
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
                    context = mContext
                    url = mContext.getString(R.string.official_site_url)
                }
            }
        }
    }

    // GitHub repository info.
    private fun createGitHubText(): SpannableString {
        val subStrToSpanGitHub = mContext.getString(R.string.info_about_githubrepo)
        val completeStrGitHub = mContext.getString(R.string.info_about_github, subStrToSpanGitHub)
        return buildSpannableString {
            string = mContext.getString(R.string.info_about_github, subStrToSpanGitHub)

            span {
                start = completeStrGitHub.indexOf(subStrToSpanGitHub)
                end = completeStrGitHub.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.info_about_githubUrl)
                }
            }

            span {
                start = completeStrGitHub.indexOf("open source")
                end = completeStrGitHub.indexOf("open source") + 11
                bold()
                increaseSizeBy(1.2f)
            }
        }
    }

    // Nerd's details.
    private fun createNerdsDetailsText(): SpannableString {
        val nerdsDetails = mContext.getString(R.string.info_about_nerd)
        val subStrToSpanGoogleApp = "Google IO 2018"
        return buildSpannableString {
            string = nerdsDetails

            span {
                start = nerdsDetails.indexOf(subStrToSpanGoogleApp)
                end = nerdsDetails.indexOf(subStrToSpanGoogleApp) + 14

                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.info_about_googleIOappUrl)
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
    }
}