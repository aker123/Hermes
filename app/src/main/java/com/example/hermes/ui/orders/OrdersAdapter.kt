package com.example.hermes.ui.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.OrdersItemBinding
import com.example.hermes.domain.models.Order
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class OrdersAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    var onItemClickListener: OnItemClickListener? = null

    var items: List<Order> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<Order> = mutableListOf()
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
        private val binding: OrdersItemBinding
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

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            binding.numberOrder.text = binding.numberOrder.resources.getString(R.string.number,order.number)
            binding.date.text = order.date
            binding.methodObtaining.text = order.method.key
            binding.delivery.text =
                when (order.method) {
                    Order.Method.DELIVERY -> order.address?.street
                    Order.Method.PICKUP -> order.shop.physicalAddress
                }
            val clientName = order.client.name
            val clientSurname = order.client.surname
            binding.client.text = "$clientName $clientSurname"
            binding.comment.text = order.comment
            binding.status.text = order.status.key
            binding.amount.text = binding.amount.resources.getString(R.string.price,order.amount.toString())
            binding.quantity.text = order.quantity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrdersItemBinding
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
        fun onItemClick(order: Order)
    }


}