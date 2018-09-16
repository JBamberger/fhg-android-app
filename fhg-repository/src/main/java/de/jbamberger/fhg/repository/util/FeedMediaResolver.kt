package de.jbamberger.fhg.repository.util

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.data.FeedMedia
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.io.IOException
import java.lang.Math.abs
import java.nio.ByteBuffer


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

data class FeedMediaRequest(val mediaId: Int)

class FeedMediaModelLoader internal constructor(
        private val api: FhgEndpoint,
        private val httpClient: OkHttpClient) : ModelLoader<FeedMediaRequest, ByteBuffer> {

    override fun buildLoadData(model: FeedMediaRequest, width: Int, height: Int, options: Options): ModelLoader.LoadData<ByteBuffer>? {
        val fetcher: DataFetcher<ByteBuffer> = FeedMediaDataFetcher(model.mediaId, width, height, api, httpClient)
        return ModelLoader.LoadData(ObjectKey(model.mediaId), fetcher)
    }

    override fun handles(model: FeedMediaRequest): Boolean {
        return true
    }
}

class FeedMediaDataFetcher internal constructor(
        mediaId: Int,
        private val width: Int,
        private val height: Int,
        api: FhgEndpoint,
        private val httpClient: OkHttpClient) : DataFetcher<ByteBuffer> {

    private val urlCall = api.getFeedMedia(mediaId)
    private var imgCall: okhttp3.Call? = null

    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun cleanup() {
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun cancel() {
        urlCall.cancel()
        imgCall?.cancel()
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        val mediaResponse: Response<FeedMedia>
        try {
            mediaResponse = urlCall.execute()
        } catch (e: IOException) {
            callback.onLoadFailed(e)
            return
        }
        if (!mediaResponse.isSuccessful) {
            callback.onLoadFailed(IOException("Server returned error"))
            return
        }

        val media = mediaResponse.body()
        if (media == null) {
            callback.onLoadFailed(IOException("Could not resolve media item"))
            return
        }

        val request = createRequest(media)
        if (request == null) {
            callback.onLoadFailed(IOException("Could not create request from media item."))
            return
        }

        imgCall = httpClient.newCall(request)

        val response: okhttp3.Response
        try {
            response = imgCall!!.execute()
        } catch (e: IOException) {
            callback.onLoadFailed(e)
            return
        }
        if (!response.isSuccessful) {
            callback.onLoadFailed(IOException("Could not load image for url"))
            return
        }
        val imgBody = response.body()
        if (imgBody == null) {
            callback.onLoadFailed(IOException("Response body is not available"))
            return
        }

        val imgBytes: ByteArray
        try {
            imgBytes = imgBody.bytes()
        } catch (e: IOException) {
            callback.onLoadFailed(e)
            return
        }

        callback.onDataReady(ByteBuffer.wrap(imgBytes))
    }

    private fun createRequest(media: FeedMedia): Request? {
        if (!media.mediaType.equals("image")) return null
        if (width <= 0 || height <= 0) return null


        val details = media.mediaDetails
        val sizes = details?.sizes
        if (details == null || sizes == null) {
            val sourceUrl = media.sourceUrl ?: return null
            return Request.Builder().url(sourceUrl).build()
        }

        val aspectRatio = width / height
        val size = width * height

        val x = listOf(sizes.thumbnail, sizes.medium, sizes.mediumLarge, sizes.large, sizes.full)
        val chosenSize = x.maxWith(Comparator { o1, o2 ->
            val asp1 = o1?.height?.let { o1.width?.div(it) }
            val size1 = o1?.height?.let { o1.width?.times(it) }
            val asp2 = o2?.height?.let { o2.width?.div(it) }
            val size2 = o2?.height?.let { o2.width?.times(it) }

            when {
                o1 == null && o2 == null -> 0
                o1 == null -> -1
                o2 == null -> 1
                o1.sourceUrl == null && o2.sourceUrl == null -> 0
                o1.sourceUrl == null -> -1
                o2.sourceUrl == null -> 1
                asp1 == null && asp2 == null -> 0
                asp1 == null -> -1
                asp2 == null -> 1
                asp1 == asp2 -> when {
                    size1 == null && size2 == null -> 0
                    size1 == null -> -1
                    size2 == null -> 1
                    else -> compareValues(abs(size1 - size), abs(size2 - size))
                }
                else -> compareValues(abs(asp1 - aspectRatio), abs(asp2 - aspectRatio))
            }
        });
        val chosenUrl = chosenSize?.sourceUrl
        if (chosenUrl == null) {
            val sourceUrl = media.sourceUrl ?: return null
            return Request.Builder().url(sourceUrl).build()
        } else {
            return Request.Builder().url(chosenUrl).build()
        }
    }
}

class FeedMediaLoaderFactory internal constructor(
        private val api: FhgEndpoint,
        private val httpClient: OkHttpClient) : ModelLoaderFactory<FeedMediaRequest, ByteBuffer> {


    override fun build(unused: MultiModelLoaderFactory): ModelLoader<FeedMediaRequest, ByteBuffer> {
        return FeedMediaModelLoader(api, httpClient)
    }

    override fun teardown() {
        // do nothing
    }
}

