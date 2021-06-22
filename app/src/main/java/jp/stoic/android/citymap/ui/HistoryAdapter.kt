package jp.stoic.android.citymap.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import jp.stoic.android.citymap.room.History

class HistoryAdapter : PagingDataAdapter<History, HistoryViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: History, newItem: History) =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.create(parent)
    }
}