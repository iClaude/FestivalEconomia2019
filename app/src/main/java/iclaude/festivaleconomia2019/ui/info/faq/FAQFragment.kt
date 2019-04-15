package iclaude.festivaleconomia2019.ui.info.faq

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import kotlinx.android.synthetic.main.collapsible_card_content.view.*
import kotlinx.android.synthetic.main.fragment_info_faq.*


class FAQFragment : Fragment(), FAQView {

    private lateinit var presenter: FAQPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = FAQPresenter(this, context!!)

        return inflater.inflate(R.layout.fragment_info_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.displayData()
    }



    companion object {
        @JvmStatic
        fun newInstance() = FAQFragment()
    }

    /**
     * Implementation of AboutView interface used to display formatted data
     * (SpannableStrings) in this Fragment.
     */

    override fun showCarInfo(spannableString: SpannableString) {
        ccCar.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()

        }
    }

    override fun showGettingAroundInfo(spannableString: SpannableString) {
        ccGettingAround.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()

        }
    }

    override fun showParkingCoachesInfo(spannableString: SpannableString) {
        ccParkingCoaches.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showParkingVansInfo(spannableString: SpannableString) {
        ccParkingVans.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showUsefulNumbersInfo(spannableString: SpannableString) {
        ccUsefulNumbers.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showHandicapInfo(spannableString: SpannableString) {
        ccHandicap.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showParkingCarsInfo(spannableString: SpannableString) {
        ccParkingCars.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showParkingCarsPaidInfo(spannableString: SpannableString) {
        ccParkingCarsPaid.tvCardDescription.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
