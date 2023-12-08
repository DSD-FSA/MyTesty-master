package com.example.mytesty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytesty.databinding.ListItemBinding

class RcAdapter(private val listener: Listener) : ListAdapter<Listitem, RcAdapter.ItemHolder>(ItemComparator()) {


    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ListItemBinding.bind(view)
        fun setData(item: Listitem, listener: Listener) = with(binding){
            Name.text = item.name
            Mac.text = item.mac
            itemView.setOnClickListener {
                listener.onClick(item)
            }
        }
        companion object{
            fun create(parent: ViewGroup): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context).
                inflate(R.layout.list_item, parent, false))
            }
        }
    }
    class ItemComparator : DiffUtil.ItemCallback<Listitem>(){
        override fun areItemsTheSame(oldItem: Listitem, newItem: Listitem): Boolean {
            return oldItem.mac == newItem.mac
        }

        override fun areContentsTheSame(oldItem: Listitem, newItem: Listitem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }
    interface Listener {
        fun onClick(item: Listitem)
        abstract fun putExtra(deviceKey: String, item: Listitem)
    }
}