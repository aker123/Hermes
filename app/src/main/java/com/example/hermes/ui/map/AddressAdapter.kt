package com.example.hermes.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.databinding.AddressItemBinding
import com.example.hermes.domain.models.Address

import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class AddressAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var onItemClickListener: OnItemClickListener? = null

    var items: List<Address> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<Address> = mutableListOf()
        set(value) {
            val callback = DefaultDiffCallback(
                oldList = field,
                newList = value,
                areContentsTheSame = { _, _ -> false }
            )
            field = value
            val result = DiffUtil.calculateDiff(callback)
            result.dispatchUpdatesTo(this)
        }

    inner class ItemHolder(
        private val binding: AddressItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            setEvents()
        }

        private fun setEvents() {

            binding.parent.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onItemClickListener?.onItemClick(_items[pos])
            }
        }

        fun bind(address: Address) {
            binding.street.text = address.street
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AddressItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = _items[position]
        when (holder) {
            is ItemHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    fun interface OnItemClickListener {
        fun onItemClick(address: Address)
    }
}