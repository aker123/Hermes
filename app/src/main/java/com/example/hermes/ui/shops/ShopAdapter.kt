package com.example.hermes.ui.shops

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.R
import com.example.hermes.databinding.ShopsItemBinding
import com.example.hermes.domain.models.Shop
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback
import java.lang.Exception


class ShopAdapter(
    val picasso: Picasso
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    var onItemClickListener: OnItemClickListener? = null

    var items: List<Shop> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<Shop> = mutableListOf()
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
        private val binding: ShopsItemBinding
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



        fun bind(shop: Shop) {
            picasso.load(shop.imagePath).placeholder(R.drawable.hermes).into(binding.image, object : Callback{
                override fun onSuccess() {
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                override fun onError(e: Exception?) {
                }

            })
            binding.name.text = shop.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ShopsItemBinding
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
        fun onItemClick(shop: Shop)
    }
}