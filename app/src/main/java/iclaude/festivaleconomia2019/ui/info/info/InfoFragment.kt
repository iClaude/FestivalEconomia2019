package iclaude.festivaleconomia2019.ui.info.info

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.utils.HeaderGridDrawable
import kotlinx.android.synthetic.main.fragment_info_info.*


class InfoFragment : Fragment(), InfoView {

    private lateinit var presenter: InfoPresenter

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = InfoPresenter(this, context!!, findNavController())

        return inflater.inflate(R.layout.fragment_info_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.displayData()
        displayImages()

        ibVideo.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = "https://www.youtube.com/watch?v=7twSBUAX2cM".toUri()
            }
            if (intent.resolveActivity(context?.packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    /**
     * Implementation of InfoView interface used to display formatted data
     * (SpannableStrings) in this Fragment.
     */

    override fun showEditorialLink(spannableString: SpannableString) {
        tvEditorialLink.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun showFocus(spannableString: SpannableString) {
        tvFocus.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun displayImages() {
        ivCineconomy.setImageDrawable(HeaderGridDrawable(context!!))
        ivDialogues.setImageDrawable(HeaderGridDrawable(context!!))
        ivForum.setImageDrawable(HeaderGridDrawable(context!!))
        ivIntersections.setImageDrawable(HeaderGridDrawable(context!!))
        ivKeywords.setImageDrawable(HeaderGridDrawable(context!!))
        ivHistory.setImageDrawable(HeaderGridDrawable(context!!))
        ivRai.setImageDrawable(HeaderGridDrawable(context!!))
        ivSpotlight.setImageDrawable(HeaderGridDrawable(context!!))
        ivWitnesses.setImageDrawable(HeaderGridDrawable(context!!))
        ivVisions.setImageDrawable(HeaderGridDrawable(context!!))
    }
}
