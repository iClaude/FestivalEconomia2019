package iclaude.festivaleconomia2019.ui.info.info

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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

        chipCineconomy.chipBackgroundColor = createChipColorStateList(Color.parseColor("#cce9c2"))
        chipDialogues.chipBackgroundColor = createChipColorStateList(Color.parseColor("#FFEAB3"))
        chipForum.chipBackgroundColor = createChipColorStateList(Color.parseColor("#A7D7FE"))
        chipIntersections.chipBackgroundColor = createChipColorStateList(Color.parseColor("#9BDD7C"))
        chipKeywords.chipBackgroundColor = createChipColorStateList(Color.parseColor("#FF7E00"))
        chipHistory.chipBackgroundColor = createChipColorStateList(Color.parseColor("#73BBF5"))
        chipRai.chipBackgroundColor = createChipColorStateList(Color.parseColor("#CC99FF"))
        chipSpotlight.chipBackgroundColor = createChipColorStateList(Color.parseColor("#D7BDE2"))
        chipWitnesses.chipBackgroundColor = createChipColorStateList(Color.parseColor("#ffccd5"))
        chipVisions.chipBackgroundColor = createChipColorStateList(Color.parseColor("#E6B0AA"))
    }

    private fun createChipColorStateList(color: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked), // checked
            intArrayOf(-android.R.attr.state_checked) // unchecked
        )

        val colors = intArrayOf(color, color)

        return ColorStateList(states, colors)
    }
}
