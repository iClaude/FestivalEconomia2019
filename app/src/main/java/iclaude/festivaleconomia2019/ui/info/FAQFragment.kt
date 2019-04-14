package iclaude.festivaleconomia2019.ui.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString
import kotlinx.android.synthetic.main.collapsible_card_content.view.*
import kotlinx.android.synthetic.main.fragment_info_faq.*


class FAQFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addLinks()
    }

    private fun addLinks() {
        val str = ccCar.card_description.text.toString()
        val spannableStr = buildSpannableString {
            string = str

            span {
                start = str.indexOf("www.viaggiareintrentino.it")
                end = start + 26
                clickableSpan {
                    context = getContext()
                    url = "www.viaggiareintrentino.it"
                }
            }
        }
        ccCar.card_description.apply {
            text = spannableStr
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FAQFragment()
    }
}
