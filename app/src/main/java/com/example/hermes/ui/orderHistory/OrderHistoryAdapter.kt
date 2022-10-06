package com.example.hermes.ui.orderHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.OrderHistoryItemBinding
import com.example.hermes.databinding.OrderHistoryProductItemBinding
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback
import java.lang.Exception

class OrderHistoryAdapter(
    val picasso: Picasso
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object {
        const val VIEW_GROUP = 0
        const val VIEW_ITEM = 1
    }

    var items: List<Any> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<Any> = mutableListOf()
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

    inner class OrderViewHolder(
        private val binding: OrderHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var opened = true

        init {
            setEvents()
        }

        private fun setEvents() {
            binding.parent.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                val item = items[pos]
                if (item is Order) {
                    if (opened) collapse(pos)
                    else expand(pos)
                }
            }

        }

        private fun expand(position: Int) {
            binding.arrowUp.visibility = View.VISIBLE
            binding.arrowDown.visibility = View.GONE
            val list: MutableList<Any> = mutableListOf()
            val positionFirstProduct = position + 1
            val item = items.let { it[position] }
            if (item is Order) {
                for (it: Product in item.products) {
                     list.add(it)
                }
                _items.addAll(positionFirstProduct, list)
                items = _items
                opened = true
                notifyItemRangeInserted(positionFirstProduct, item.products.size)
            }
        }

        private fun collapse(position: Int) {
            binding.arrowUp.visibility = View.GONE
            binding.arrowDown.visibility = View.VISIBLE
            val positionFirstProduct = position + 1
            val item = items.let { it[position] }
            if (item is Order) {
                for (it: Product in item.products) {
                    _items.remove(it)
                }
                opened = false
                items = _items
                notifyItemRangeRemoved(positionFirstProduct, item.products.size)
            }

        }

        fun bind(order: Order) {
            picasso.load(order.shop.imagePath).placeholder(R.drawable.hermes).into(binding.image, object :
                Callback {
                override fun onSuccess() {
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                override fun onError(e: Exception?) {
                }

            })
            binding.numberOrder.text = order.number
            binding.name.text = order.shop.name
            binding.status.text = order.status.key
            binding.amount.text = binding.amount.resources.getString(R.string.price,order.amount.toString())
        }
    }

    inner class ProductViewHolder(
        private val binding: OrderHistoryProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.name.text = product.name
            binding.amount.text = binding.amount.resources.getString(R.string.price,product.amount.toString())
            binding.quantity.text =  binding.quantity.resources.getString(R.string.quantity, product.quantity.toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position] is Order  -> VIEW_GROUP
            else -> VIEW_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_ITEM -> {
                val binding = OrderHistoryProductItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ProductViewHolder(binding)
            }
            else -> {
                val binding = OrderHistoryItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                OrderViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = _items[position]
        when (holder) {
            is ProductViewHolder -> if (item is Product) holder.bind(item)
            is OrderViewHolder -> if (item is Order) holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return _items.size
    }

}