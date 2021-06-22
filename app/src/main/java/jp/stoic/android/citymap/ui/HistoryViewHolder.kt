package jp.stoic.android.citymap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.stoic.android.citymap.databinding.ItemHistoryBinding
import jp.stoic.android.citymap.room.History

class HistoryViewHolder(
    private val binding: ItemHistoryBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): HistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHistoryBinding.inflate(inflater, parent, false)
            return HistoryViewHolder(binding)
        }
    }

    fun bind(item: History?) {
        binding.itemNumber.text = item?.code
        binding.content.text = item?.name
    }
}