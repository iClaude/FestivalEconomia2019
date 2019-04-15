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
    fun showParkingCoachesInfo(spannableString: SpannableString)
    fun showParkingVansInfo(spannableString: SpannableString)
    fun showUsefulNumbersInfo(spannableString: SpannableString)
    fun showHandicapInfo(spannableString: SpannableString)

}

class FAQPresenter(private var faqView: FAQView, private var mContext: Context) {

    // Display all the formatted data in the associated View.
    fun displayData() {
        faqView.showCarInfo(createCarInfoText())
        faqView.showParkingCoachesInfo(createParkingCoachesText())
        faqView.showParkingVansInfo(createParkingVansText())
        faqView.showUsefulNumbersInfo(createUsefulNumbersText())
        faqView.showHandicapInfo(createHandicapInfoText())
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

    private fun createParkingCoachesText(): SpannableString {
        val str = mContext.getString(R.string.faq_parkingcoaches_description)

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf("Trento-Monte Bondone")
                end = start + 20
                clickableSpan {
                    context = mContext
                    url = "http://www.comune.trento.it/content/view/full/221253"
                }
            }
        }
    }

    private fun createParkingVansText(): SpannableString {
        val str = mContext.getString(R.string.faq_parkingvans_description)

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf("http://www.trentinomobilita.it")
                end = start + 30
                clickableSpan {
                    context = mContext
                    url = "http://www.trentinomobilita.it"
                }
            }
        }
    }

    private fun createUsefulNumbersText(): SpannableString {
        val chemistsListStr = mContext.getString(R.string.faq_usefulenumbers_chemists_list)
        val chemistsMapStr = mContext.getString(R.string.faq_usefulenumbers_chemists_map)
        val hospitalStr = mContext.getString(R.string.faq_usefulenumbers_hospital)
        val emergencyStr = mContext.getString(R.string.faq_usefulenumbers_emergency_medical)
        val str = mContext.getString(
            R.string.faq_usefulnumbers_description,
            chemistsListStr,
            chemistsMapStr,
            hospitalStr,
            emergencyStr
        )

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf(chemistsListStr)
                end = start + chemistsListStr.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.faq_usefulenumbers_chemists_list_link)
                }
            }

            span {
                start = str.indexOf(chemistsMapStr)
                end = start + chemistsMapStr.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.faq_usefulenumbers_chemists_map_link)
                }
            }

            span {
                start = str.indexOf(hospitalStr)
                end = start + hospitalStr.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.faq_usefulenumbers_hospital_link)
                }
            }

            span {
                start = str.indexOf(emergencyStr)
                end = start + emergencyStr.length
                clickableSpan {
                    context = mContext
                    url = mContext.getString(R.string.faq_usefulenumbers_emergency_medical_link)
                }
            }
        }
    }

    private fun createHandicapInfoText(): SpannableString {
        val str = mContext.getString(R.string.faq_handicap_description)

        return buildSpannableString {
            string = str

            span {
                start = str.indexOf("www.handicrea.it")
                end = start + 16
                clickableSpan {
                    context = mContext
                    url = "http://www.handicrea.it/"
                }
            }

            span {
                start = str.indexOf("www.ttesercizio.it.")
                end = start + 18
                clickableSpan {
                    context = mContext
                    url = "https://www.trentinotrasporti.it/"
                }
            }
        }
    }
}