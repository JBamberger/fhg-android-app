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
        val color = when {
            item.isMarkedNew -> R.color.vplan_background_new
            item.isOmitted -> R.color.vplan_background_omitted
            else -> R.color.vplan_background_standard
        }
        view.background = ColorDrawable(ContextCompat.getColor(view.context, color))
    }

    @JvmStatic
    @BindingAdapter("vPlanTextColor")
    fun bindTextColor(view: TextView, item: VPlanRow) {
         val color = when {
            item.isMarkedNew -> R.color.vplan_text_new
            item.isOmitted -> R.color.vplan_text_omitted
            else -> R.color.vplan_text_default
        }
        view.setTextColor(ContextCompat.getColor(view.context, color))
    }

    @JvmStatic
    @BindingAdapter("vPlanTextColorHighlighted")
    fun bindTextColorHighlighted(view: TextView, item: VPlanRow) {
        val color = when {
            item.isMarkedNew -> R.color.vplan_text_new
            item.isOmitted -> R.color.vplan_text_omitted_highlight
            else -> R.color.vplan_text_default
        }
        view.setTextColor(ContextCompat.getColor(view.context, color))
    }
}