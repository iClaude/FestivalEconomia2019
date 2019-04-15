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
    fun showGettingAroundInfo(spannableString: SpannableString)
    fun showParkingCoachesInfo(spannableString: SpannableString)
    fun showParkingVansInfo(spannableString: SpannableString)
    fun showParkingCarsInfo(spannableString: SpannableString)
    fun showParkingCarsPaidInfo(spannableString: SpannableString)
    fun showUsefulNumbersInfo(spannableString: SpannableString)
    fun showHandicapInfo(spannableString: SpannableString)

}

class FAQPresenter(private var faqView: FAQView, private var mContext: Context) {

    // Display all the formatted data in the associated View.
    fun displayData() {
        faqView.showCarInfo(createCarInfoText())
        faqView.showGettingAroundInfo(createGettingAroundInfoText())
        faqView.showParkingCoachesInfo(createParkingCoachesText())
        faqView.showParkingVansInfo(createParkingVansText())
        faqView.showParkingCarsInfo(createParkingCarsText())
        faqView.showParkingCarsPaidInfo(createParkingCarsPaidText())
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

            span {
                start = str.indexOf("Brennero")
                end = start + 8
                bold()
            }

            span {
                start = str.indexOf("Padova, Bassano, Belluno")
                end = start + 24
                bold()
            }

            span {
                start = str.indexOf("Lago di Garda")
                end = start + 13
                bold()
            }
        }
    }

    private fun createGettingAroundInfoText(): SpannableString {
        val str = mContext.getString(R.string.faq_gettingaround_description)

        return buildSpannableString {
            string = str
            span {
                start = str.indexOf("Bicigrill in Piazza Fiera")
                end = start + 25
                bold()
            }
        }
    }

    private fun createParkingCoachesText(): SpannableString {
        val item1 = mContext.getString(R.string.faq_parkingcoaches_description_item1)
        val item2 = mContext.getString(R.string.faq_parkingcoaches_description_item2)
        val str = mContext.getString(R.string.faq_parkingcoaches_description, item1, item2)

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

            span {
                start = str.indexOf(item1)
                end = start + item1.length
                bold()
            }

            span {
                start = str.indexOf(item2)
                end = start + item2.length
                bold()
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

    private fun createParkingCarsText(): SpannableString {
        val str = mContext.getString(R.string.faq_parkingcars_description)

        return buildSpannableString {
            string = str

            var pos = 0
            repeat(6) {
                span {
                    start = pos
                    end = str.indexOf("\n", pos)
                    pos = end + 1

                    bullet()
                }
            }

            span {
                start = str.indexOf("Ex Zuffo – Monte Baldo – Ghiaie (c/o PalaTrento)")
                end = start + 48
                bold()
                underline()
            }
        }
    }

    private fun createParkingCarsPaidText(): SpannableString {
        val str = mContext.getString(R.string.faq_parkingcars_paid_description)

        return buildSpannableString {
            string = str

            var pos = 0
            while (pos < str.length) {
                span {
                    start = pos
                    end = str.indexOf("\n", pos)
                    if (end == -1) end = str.length
                    pos = end + 1

                    bullet()
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