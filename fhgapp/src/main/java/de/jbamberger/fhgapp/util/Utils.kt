package de.jbamberger.fhgapp.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import de.jbamberger.fhgapp.BuildConfig
import de.jbamberger.fhgapp.R


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class Utils {
    companion object {
        fun shareApplication(activity: Activity) {
            val chooserTitle = activity.getString(R.string.action_share_chooser_title)
            val content = activity.getString(R.string.action_share_text,
                    activity.getString(R.string.app_name),
                    activity.getString(R.string.play_link, BuildConfig.APPLICATION_ID))
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, content)
            activity.startActivity(Intent.createChooser(sendIntent, chooserTitle))
        }

        fun contactDeveloper(activity: Activity) {
            val recipientUri = Uri.parse(activity.getString(R.string.support_uri))
            val subject = activity.getString(R.string.support_subject, BuildConfig.APPLICATION_ID,
                    BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
            val intent = Intent(Intent.ACTION_SENDTO, recipientUri)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            activity.startActivity(intent)
        }
    }
}