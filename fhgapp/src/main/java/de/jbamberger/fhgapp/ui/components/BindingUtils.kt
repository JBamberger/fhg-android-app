package de.jbamberger.fhgapp.ui.components

import android.databinding.BindingAdapter
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.View
import android.widget.TextView
import de.jbamberger.api.data.VPlanRow
import de.jbamberger.fhgapp.R

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object BindingUtils {

    @JvmStatic
    @BindingAdapter("html")
    fun bindHtml(view: TextView, html: String) {
        val span = Html.fromHtml(html);
        view.text = span
    }

    @JvmStatic
    @BindingAdapter("vplanBackground")
    fun bindBackgroundColor(view: View, item: VPlanRow) {
        if (item.isMarkedNew) {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_new))
        } else if (item.isOmitted) {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_omitted))
        } else {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_standard))
        }
    }
}