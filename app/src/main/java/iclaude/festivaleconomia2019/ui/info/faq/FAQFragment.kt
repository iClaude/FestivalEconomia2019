package iclaude.festivaleconomia2019.ui.info.faq

import android.os.Bundle
import android.text.SpannableString
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
        ccCar.tvCardDescription.text = spannableString
    }
}
