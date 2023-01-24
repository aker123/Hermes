package com.example.hermes.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.*
import com.example.hermes.domain.models.Product
import com.google.android.material.chip.Chip
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback
import java.lang.Exception

class ProductsAdapter(
    val picasso: Picasso
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var onAddBasketClickListener: OnAddBasketClickListener? = null
    var onCheckedStateChangeListener: OnCheckedStateChangeListener? = null

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

    inner class ItemViewHolder(
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

            binding.addBasket.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onAddBasketClickListener?.onAddBasketClick(_items[pos])
            }

            binding.sizes.setOnCheckedStateChangeListener { group, checkedIds ->
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnCheckedStateChangeListener
                }
                var chip: Chip? = null
                if (checkedIds.isNotEmpty()) chip = group.findViewById(checkedIds[0])
                onCheckedStateChangeListener?.onCheckedStateChange(
                    _items[pos],
                    chip?.text?.toString() ?: ""
                )
            }
        }

        fun bind(product: Product) {
            picasso.load(product.imagePath).placeholder(R.drawable.hermes).into(binding.image)

            if (binding.sizes.childCount == 0) {
                product.sizes.sortedBy { it.value }.forEach { size ->
                    val chip = ChipChoiceBinding
                        .inflate(LayoutInflater.from(binding.sizes.context), binding.sizes, false)
                    chip.root.text = size.value
                    binding.sizes.addView(chip.root)
                }
            }

            binding.name.text = product.name
            binding.price.text =
                binding.price.resources.getString(R.string.price, product.price.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ProductsItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = _items[position]
        when (holder) {
            is ItemViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    fun interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    fun interface OnCheckedStateChangeListener {
        fun onCheckedStateChange(product: Product, size: String)
    }

    fun interface OnAddBasketClickListener {
        fun onAddBasketClick(product: Product)
    }
}