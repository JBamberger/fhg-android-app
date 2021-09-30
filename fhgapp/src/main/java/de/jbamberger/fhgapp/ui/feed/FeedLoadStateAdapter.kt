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
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.ui.BindingUtils

internal class FeedLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<FeedLoadStateAdapter.ErrorHolder>() {


    override fun onBindViewHolder(holder: ErrorHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ErrorHolder {
        return ErrorHolder.create(parent, retry)
    }

    class ErrorHolder(view: View, private val retry: () -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
        private val retryButton = view.findViewById<Button>(R.id.retry_button).also {
            it.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            BindingUtils.bindVisibility(progressBar, loadState is LoadState.Loading)
            val isError = loadState is LoadState.Error
            BindingUtils.bindVisibility(retryButton, isError)
            BindingUtils.bindVisibility(errorMsg, isError)
            errorMsg.text = if (isError) (loadState as LoadState.Error).error.message else null
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): ErrorHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.network_state_item, parent, false)
                return ErrorHolder(view, retryCallback)
            }
        }
    }
}
