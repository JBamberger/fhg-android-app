package de.jbamberger.fhgapp.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhgapp.App
import java.io.InputStream


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@GlideModule
class FhgGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            FeedMedia::class.java,
            InputStream::class.java,
            (context.applicationContext as App).repoHelper.provideFeedMediaLoaderFactory()
        )
    }
}