package de.jbamberger.fhg.repository.util

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import de.jbamberger.fhg.repository.data.FeedMedia
import java.io.InputStream
import java.lang.Math.abs


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FeedMediaLoader private constructor(
        urlLoader: ModelLoader<GlideUrl, InputStream>) : BaseGlideUrlLoader<FeedMedia>(urlLoader) {

    override fun handles(model: FeedMedia): Boolean {
        return true
    }

    override fun getUrl(model: FeedMedia?, width: Int, height: Int, options: Options?): String? {
        if (model == null || model.media_type != "image" || width <= 0 || height <= 0) {
            return null
        }

        val aspectRatio = width / height
        val size = width * height

        val x = model.media_details.sizes.values
        val chosenSize = x.maxWithOrNull { o1, o2 ->
            val asp1 = o1.width / o1.height
            val size1 = o1.width * o1.height
            val asp2 = o2.width / o2.height
            val size2 = o2.width * o2.height

            when (asp1) {
                asp2 -> compareValues(abs(size1 - size), abs(size2 - size))
                else -> compareValues(abs(asp1 - aspectRatio), abs(asp2 - aspectRatio))
            }
        }
        return chosenSize?.source_url ?: model.source_url
    }

    class Factory : ModelLoaderFactory<FeedMedia, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<FeedMedia, InputStream> {
            return FeedMediaLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
        }

        override fun teardown() {}
    }
}




