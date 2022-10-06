package com.example.hermes.ui.orderManager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.ChipChoiceBinding
import com.example.hermes.databinding.OrderProductItemBinding
import com.example.hermes.domain.models.Product
import com.squareup.picasso.Picasso
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class OrderManagerAdapter(
    val picasso: Picasso
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
        private val binding: OrderProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            picasso.load(product.imagePath).placeholder(R.drawable.hermes).into(binding.image)

            if (binding.sizes.childCount == 0) {
                product.sizes.sortedBy { it.value }.forEach { size ->
                    val chip = ChipChoiceBinding
                        .inflate(LayoutInflater.from(binding.sizes.context), binding.sizes, false)
                    chip.root.text = size.value
                    chip.root.isChecked = size.selected
                    chip.root.isClickable = false
                    binding.sizes.addView(chip.root)
                }
            }

            binding.price.text = binding.price.resources.getString(R.string.price,product.price.toString())
            binding.name.text = product.name
            binding.amount.text = binding.amount.resources.getString(R.string.price,product.amount.toString())
            binding.quantity.text = binding.quantity.resources.getString(R.string.quantity,product.quantity.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrderProductItemBinding
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

}