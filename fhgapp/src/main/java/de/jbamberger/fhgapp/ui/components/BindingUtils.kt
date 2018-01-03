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
    @BindingAdapter("visibleIfNotEmpty")
    fun bindVisibilityIfEmpty(view: View, value: String) {
        view.visibility = if (!value.isBlank()) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun bindVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("vPlanBackground")
    fun bindBackgroundColor(view: View, item: VPlanRow) {
        if (item.isMarkedNew) {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_background_new))
        } else if (item.isOmitted) {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_background_omitted))
        } else {
            view.background =
                    ColorDrawable(ContextCompat.getColor(view.context, R.color.vplan_background_standard))
        }
    }

    @JvmStatic
    @BindingAdapter("vPlanTextColor")
    fun bindTextColor(view: TextView, item: VPlanRow) {
        if (item.isMarkedNew) {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_new))
        } else if (item.isOmitted) {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_omitted))
        } else {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_default))
        }
    }

    @JvmStatic
    @BindingAdapter("vPlanTextColorHighlighted")
    fun bindTextColorHighlighted(view: TextView, item: VPlanRow) {
        if (item.isOmitted) {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_omitted_highlight))
        } else if (item.isMarkedNew) {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_new))
        } else {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.vplan_text_default))
        }
    }
}