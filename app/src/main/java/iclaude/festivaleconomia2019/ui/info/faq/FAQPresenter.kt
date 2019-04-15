package iclaude.festivaleconomia2019.ui.info.faq

import android.content.Context
import android.text.SpannableString
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString

/**
 * Classes, implementing the MVP pattern, used to provide formatted Strings to
 * FAQFragment to display.
 */

interface FAQView {
    fun showCarInfo(spannableString: SpannableString)

}

class FAQPresenter(private var faqView: FAQView, private var mContext: Context) {

    // Display all the formatted data in the associated View.
    fun displayData() {
        faqView.showCarInfo(createCarInfoText())

    }

    /**
     * Create SpannableStrings with desired formats.
     */

    private fun createCarInfoText(): SpannableString {
        val str = mContext.getString(R.string.faq_car_description)

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf("www.viaggiareintrentino.it")
                end = start + 26
                clickableSpan {
                    context = mContext
                    url = "http://www.viaggiareintrentino.it"
                }
            }
        }
    }
}