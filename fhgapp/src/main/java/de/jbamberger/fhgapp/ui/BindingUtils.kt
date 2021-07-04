/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.fhgapp.ui

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import de.jbamberger.fhgapp.repository.data.VPlanRow
import de.jbamberger.fhgapp.R


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object BindingUtils {

    @JvmStatic
    @BindingAdapter("html")
    fun bindHtml(view: TextView, html: String?) {
        view.text = when {
            html == null -> ""
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                html,
                Html.FROM_HTML_MODE_LEGACY
            )
            else -> @Suppress("DEPRECATION") Html.fromHtml(html)
        }
    }

    @JvmStatic
    @BindingAdapter("stripHtml")
    fun bindStrippingHtml(view: TextView, html: String?) {
        view.text = when {
            html == null -> ""
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                html,
                Html.FROM_HTML_MODE_LEGACY
            ).toString()
            else -> @Suppress("DEPRECATION") Html.fromHtml(html).toString()
        }
    }

    @JvmStatic
    @BindingAdapter("visibleIfNotEmpty")
    fun bindVisibilityIfEmpty(view: View, value: String?) {
        view.visibility = if (value == null || value.isBlank()) View.GONE else View.VISIBLE
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

    @JvmStatic
    @BindingAdapter("leftVector")
    fun bindLeftVectorDrawable(view: TextView, @DrawableRes icon: Int) {
        val x = view.compoundDrawables
        view.setCompoundDrawablesWithIntrinsicBounds(
            AppCompatResources.getDrawable(view.context, icon), x[1], x[2], x[3]
        )
    }

    @JvmStatic
    @BindingAdapter("topVector")
    fun bindTopVectorDrawable(view: TextView, @DrawableRes icon: Int) {
        val x = view.compoundDrawables
        view.setCompoundDrawablesWithIntrinsicBounds(
            x[0], AppCompatResources.getDrawable(view.context, icon), x[2], x[3]
        )
    }
}