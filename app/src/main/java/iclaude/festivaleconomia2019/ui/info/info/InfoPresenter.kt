package iclaude.festivaleconomia2019.ui.info.info

import android.content.Context
import android.text.SpannableString
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString

/**
 * Classes, implementing the MVP pattern, used to provide formatted Strings to
 * InfoFragment to display.
 */

interface InfoView {
    fun showEditorialLink(spannableString: SpannableString)
    fun showFocus(spannableString: SpannableString)

}

class InfoPresenter(private var infoView: InfoView, private var mContext: Context) {

    // Display all the formatted data in the associated View.
    fun displayData() {
        infoView.showEditorialLink(createEditorialLinkText())
        infoView.showFocus(createFocusText())

    }

    /**
     * Create SpannableStrings with desired formats.
     */

    private fun createEditorialLinkText(): SpannableString {
        val str = mContext.getString(R.string.info_editorial_link)

        return buildSpannableString {
            string = str

            span {
                start = 0
                end = str.length

                bold()
                clickableSpan {
                    context = mContext
                    url = "https://2019.festivaleconomia.eu/"
                }
            }
        }
    }

    private fun createFocusText(): SpannableString {
        val item1 = mContext.getString(R.string.info_focus_item1)
        val item2 = mContext.getString(R.string.info_focus_item2)
        val item3 = mContext.getString(R.string.info_focus_item3)
        val item4 = mContext.getString(R.string.info_focus_item4)
        val item5 = mContext.getString(R.string.info_focus_item5)
        val item6 = mContext.getString(R.string.info_focus_item6)
        val item7 = mContext.getString(R.string.info_focus_item7)
        val str = mContext.getString(R.string.info_focus, item1, item2, item3, item4, item5, item6, item7)

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf(item1)
                end = start + item1.length
                bold()
            }

            span {
                start = str.indexOf(item2)
                end = start + item2.length
                bold()
                italic()
            }

            span {
                start = str.indexOf(item3)
                end = start + item3.length
                bold()
            }

            span {
                start = str.indexOf(item4)
                end = start + item4.length
                bold()
            }

            span {
                start = str.indexOf(item5)
                end = start + item5.length
                bold()
            }

            span {
                start = str.indexOf(item6)
                end = start + item6.length
                bold()
            }

            span {
                start = str.indexOf(item7)
                end = start + item7.length
                bold()
            }
        }
    }
}