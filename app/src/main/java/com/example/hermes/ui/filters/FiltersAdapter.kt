package com.example.hermes.ui.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.databinding.FilterCheckboxBinding
import com.example.hermes.domain.filters.Filter
import com.example.hermes.domain.filters.FilterToggle
import com.example.hermes.domain.models.Address
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback

class FiltersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FILTER = 1
        private const val TYPE_FILTER_TOGGLE = 2
    }

    var onItemClickListener: OnItemClickListener? = null
    var onMenuClickListener: OnMenuClickListener? = null
    var onCheckedChangeListener: OnCheckedChangeListener? = null
    var onToggleChangeListener: OnToggleChangeListener? = null

    var items: List<Filter<*>> = listOf()
        set(value) {
            field = value
            _items = value.toMutableList()
        }

    private var _items: MutableList<out Filter<*>> = mutableListOf()
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

    private inner class FilterViewHolder(
        private val binding: FilterCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            setEvents()
        }

        private fun setEvents() {
            binding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onItemClickListener?.onItemClick(items[pos])
            }

            binding.iconMenu.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                onMenuClickListener?.onMenuClick(items[pos])
            }

            binding.checkbox.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                val filter = items[pos]
                val state = binding.checkbox.isChecked

                onCheckedChangeListener?.onCheckedChanged(filter, state)
            }
        }

        fun bind(filter: Filter<*>) {
            binding.checkbox.isChecked = filter.enable
            binding.name.text = filter.name

            binding.iconMenu.visibility =
                if (filter !is FilterToggle<*> && filter.filterGroup.filters.size > 0 &&
                    (filter.filterGroup.filters.size > 1 ||
                            filter.filterGroup.filters.firstOrNull() !is FilterToggle<*>)
                ) View.VISIBLE
                else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            FilterCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is FilterViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun interface OnItemClickListener {
        fun onItemClick(filter: Filter<*>)
    }

    fun interface OnMenuClickListener {
        fun onMenuClick(filter: Filter<*>)
    }

    fun interface OnCheckedChangeListener {
        fun onCheckedChanged(filter: Filter<*>, state: Boolean)
    }

    fun interface OnToggleChangeListener {
        fun onToggleChanged(filter: FilterToggle<*>, state: Filter<*>)
    }
}