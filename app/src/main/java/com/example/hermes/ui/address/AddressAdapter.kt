package com.example.hermes.ui.address

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.AddressItemVerticalBinding
import com.example.hermes.domain.models.Address
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class AddressAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onEditingClickListener: OnEditingClickListener? = null
    var onDeleteClickListener: OnDeleteClickListener? = null


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
        private val binding: AddressItemVerticalBinding
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

            binding.editing.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onEditingClickListener?.onEditingClick(_items[pos])
            }

            binding.delete.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onDeleteClickListener?.onDeleteClick(_items[pos])
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(address: Address) {
            if (address.active) {
                binding.image.setImageResource(R.drawable.ic_location_main_24)
                binding.editing.setImageResource(R.drawable.ic_pen_main_24)
            } else {
                binding.image.setImageResource(R.drawable.ic_location_24)
                binding.editing.setImageResource(R.drawable.ic_pen_24)
            }
            binding.street.text = address.street
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AddressItemVerticalBinding
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

    fun interface OnEditingClickListener {
        fun onEditingClick(address: Address)
    }

    fun interface OnDeleteClickListener {
        fun onDeleteClick(address: Address)
    }
}