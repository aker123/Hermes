package com.example.hermes.ui.basket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.BasketItemBinding
import com.example.hermes.databinding.ChipChoiceBinding
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.products.ProductsAdapter
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class BasketAdapter(
    val picasso: Picasso
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onAddClickListener: OnAddClickListener? = null
    var onRemoveClickListener: OnRemoveClickListener? = null
    var onCheckedStateChangeListener : OnCheckedStateChangeListener? = null

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
        private val binding: BasketItemBinding
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

            binding.sizes.setOnCheckedStateChangeListener { group, checkedIds ->
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnCheckedStateChangeListener
                }
                var chip: Chip? =  null
                if (checkedIds.isNotEmpty())  chip = group.findViewById(checkedIds[0])
                onCheckedStateChangeListener?.onCheckedStateChange(_items[pos],chip?.text?.toString() ?: "")
            }
        }

        fun bind(product: Product) {
            picasso.load(product.imagePath).placeholder(R.drawable.hermes).into(binding.image)

            if (binding.sizes.childCount == 0) {
                product.sizes.sortedBy { it.value }.forEach { size ->
                    val chip = ChipChoiceBinding
                        .inflate(LayoutInflater.from(binding.sizes.context), binding.sizes, false)
                    chip.root.text = size.value
                    chip.root.isChecked = size.selected
                    binding.sizes.addView(chip.root)
                }
            }

            binding.name.text = product.name
            binding.amount.text = product.amount.toString()
            binding.quantity.text = product.quantity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BasketItemBinding
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

    fun interface OnCheckedStateChangeListener {
        fun onCheckedStateChange(product: Product, size: String)
    }

}