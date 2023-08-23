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

package de.jbamberger.fhgapp.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.ui.BindingUtils
import de.jbamberger.fhgapp.util.Utils

internal class FeedAdapter(private val glide: RequestManager) :
    PagingDataAdapter<Pair<FeedItem, FeedMedia?>, FeedAdapter.FeedItemHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemHolder {
        return FeedItemHolder.create(glide, parent)
    }

    override fun onBindViewHolder(holder: FeedItemHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Pair<FeedItem, FeedMedia?>>() {
            override fun areItemsTheSame(
                oldItem: Pair<FeedItem, FeedMedia?>, newItem: Pair<FeedItem, FeedMedia?>
            ) = oldItem.first.id == newItem.first.id

            override fun areContentsTheSame(
                oldItem: Pair<FeedItem, FeedMedia?>, newItem: Pair<FeedItem, FeedMedia?>
            ) = oldItem == newItem
        }
    }

    class FeedItemHolder(
        private val glide: RequestManager, view: View
    ) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.title)
        private val featuredMedia = view.findViewById<ImageView>(R.id.featured_media)

        private val textLoading = view.context.getString(R.string.feed_status_loading)

        private var post: Pair<FeedItem, FeedMedia?>? = null
        private var link: String? = null

        init {
            view.setOnClickListener {
                this.link?.let { Utils.openUrl(view.context, it) }
            }
        }

        private fun FeedMedia.selectMediaVariant(): FeedMedia.ImageSize? {
            val sizes = this.media_details.sizes
            return try {
                sizes.entries.first { it.key == "thumbnail" }.value
            } catch (e: NoSuchElementException) {
                sizes.values.firstOrNull()
            }
        }

        fun bind(post: Pair<FeedItem, FeedMedia?>?) {
            this.post = post
            this.link = post?.first?.link

            if (post == null) {
                this.post = null
                this.link = null
            } else {
                val item = post.first
                val itemTitle = item.title?.rendered
                val itemExcerpt = item.excerpt?.rendered

                val titleString = when {
                    itemTitle?.isNotBlank() == true -> itemTitle
                    itemExcerpt?.isNotBlank() == true -> itemExcerpt
                    else -> itemView.context.getString(R.string.feed_item_no_title)
                }
                BindingUtils.bindStrippingHtml(title, titleString)
            }

            val media = post?.second
            val variant = post?.second?.selectMediaVariant()
            if (media != null && variant != null) {
                featuredMedia.visibility = View.VISIBLE
                featuredMedia.contentDescription = when {
                    media.caption.rendered.isNotBlank() -> media.caption.rendered
                    else -> featuredMedia.context.getString(R.string.feed_media_content_desctiption)
                }
                glide.load(variant.source_url)
                    .centerInside()
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .fallback(R.drawable.ic_broken_image_black_24dp)
                    .into(featuredMedia)
            } else {
                featuredMedia.visibility = View.INVISIBLE
                featuredMedia.contentDescription = featuredMedia.context
                    .getString(R.string.feed_media_content_desctiption)
                glide.clear(featuredMedia)
            }
        }

        companion object {
            fun create(glide: RequestManager, parent: ViewGroup): FeedItemHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_item, parent, false)
                return FeedItemHolder(glide, view)
            }
        }
    }

}
