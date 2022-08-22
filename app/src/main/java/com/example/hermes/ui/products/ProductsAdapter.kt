package com.example.hermes.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.databinding.ProductsItemBinding
import com.example.hermes.domain.models.Product
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class ProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onAddClickListener: OnAddClickListener? = null
    var onRemoveClickListener: OnRemoveClickListener? = null
    var onPriceClickListener: OnPriceClickListener? = null

    var items: List<Product> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<Product> = mutableListOf()
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
        private val binding: ProductsItemBinding
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

            binding.addBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onAddClickListener?.onAddClick(_items[pos])
            }

            binding.removeBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onRemoveClickListener?.onRemoveClick(_items[pos])
            }

            binding.priceBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onPriceClickListener?.onPriceClick(_items[pos])
            }
        }

        fun bind(product: Product) {
            binding.layoutQuantity.isVisible = product.quantity > 0
            binding.priceBtn.isVisible = product.quantity == 0L
            binding.name.text = product.name
            if (product.amount == 0L) binding.amount.text = ""
            else binding.amount.text = product.amount.toString()
            binding.quantity.text = product.quantity.toString()
            binding.priceBtn.text = product.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ProductsItemBinding
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
        fun onItemClick(product: Product)
    }

    fun interface OnAddClickListener {
        fun onAddClick(product: Product)
    }

    fun interface OnRemoveClickListener {
        fun onRemoveClick(product: Product)
    }

    fun interface OnPriceClickListener {
        fun onPriceClick(product: Product)
    }
}