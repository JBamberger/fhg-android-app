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

package de.jbamberger.fhgapp.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import de.jbamberger.fhgapp.BuildConfig
import de.jbamberger.fhgapp.R
import timber.log.Timber


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object Utils {
    fun shareApplication(activity: Activity) {
        val chooserTitle = activity.getString(R.string.action_share_chooser_title)
        val content = activity.getString(
            R.string.action_share_text,
            activity.getString(R.string.app_name),
            activity.getString(R.string.play_link, BuildConfig.APPLICATION_ID)
        )
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        val intent = Intent.createChooser(sendIntent, chooserTitle)

        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.i(e, "Could not find app capable of sharing.")
        }
    }

    fun contactDeveloper(activity: Activity) {
        val recipientUri = Uri.parse(activity.getString(R.string.support_uri))
        val subject = activity.getString(
            R.string.support_subject, BuildConfig.APPLICATION_ID,
            BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE
        )
        val intent = Intent(Intent.ACTION_SENDTO, recipientUri)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.i(e, "Could not find email application.")
        }
    }

    fun openUrl(context: Context, url: String) {
        var uri = Uri.parse(url)
        if (uri.scheme?.isBlank() == true) {
            uri = Uri.parse("http://$url")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.i(e, "Could not find app capable of handling urls.")
        }
    }

    fun openUrl(context: Context, urlId: Int) {
        openUrl(context, context.getString(urlId))
    }
}