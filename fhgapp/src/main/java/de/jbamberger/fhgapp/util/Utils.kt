package de.jbamberger.fhgapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import de.jbamberger.fhgapp.BuildConfig
import de.jbamberger.fhgapp.R


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object Utils {
    fun shareApplication(activity: Activity) {
        val chooserTitle = activity.getString(R.string.action_share_chooser_title)
        val content = activity.getString(R.string.action_share_text,
                activity.getString(R.string.app_name),
                activity.getString(R.string.play_link, BuildConfig.APPLICATION_ID))
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        val intent = Intent.createChooser(sendIntent, chooserTitle)

        if (intent.resolveActivity(activity.packageManager) == null) return
        activity.startActivity(intent)
    }

    fun contactDeveloper(activity: Activity) {
        val recipientUri = Uri.parse(activity.getString(R.string.support_uri))
        val subject = activity.getString(R.string.support_subject, BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        val intent = Intent(Intent.ACTION_SENDTO, recipientUri)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        if (intent.resolveActivity(activity.packageManager) == null) return
        activity.startActivity(intent)
    }

    fun openUrl(context: Context, url: String) {
        var uri = Uri.parse(url)
        if (uri.scheme?.isBlank() == true) {
            uri = Uri.parse("http://$url")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (intent.resolveActivity(context.packageManager) == null) return
        context.startActivity(intent)
    }

    fun openUrl(context: Context, urlId: Int) {
        openUrl(context, context.getString(urlId))
    }
}