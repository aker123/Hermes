package com.example.hermes.ui.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hermes.databinding.FilterCheckboxBinding
import com.example.hermes.databinding.FilterIntervalNumbersBinding
import com.example.hermes.domain.filters.Filter
import com.example.hermes.domain.filters.FilterToggle
import com.example.hermes.domain.filters.conditions.ConditionDate
import com.example.hermes.domain.filters.conditions.ConditionIntervalNumbers
import com.example.hermes.domain.models.Address
import com.google.android.material.slider.RangeSlider
import ru.aptrade.fobos30.UI.FobosInterfaceLibrary.Adapters.util.DefaultDiffCallback
import java.util.*

class FiltersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FILTER = 1
        private const val TYPE_FILTER_TOGGLE = 2
        private const val TYPE_FILTER_INTERVAL_NUMBERS = 3
    }

    var onItemClickListener: OnItemClickListener? = null
    var onMenuClickListener: OnMenuClickListener? = null
    var onCheckedChangeListener: OnCheckedChangeListener? = null
    var onToggleChangeListener: OnToggleChangeListener? = null
    var onIntervalChangeListener: OnIntervalChangeListener? = null

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

            binding.subName.text =
                if (filter.condition is ConditionDate<*>) {
                    val c = filter.condition.date
                    String.format("%02d-%02d-%04d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
                }
                else ""

            binding.subName.visibility =
                if (filter.condition is ConditionDate<*>) View.VISIBLE
                else View.GONE

            binding.iconMenu.visibility =
                if (filter !is FilterToggle<*> && filter.filterGroup.filters.size > 0 &&
                    (filter.filterGroup.filters.size > 1 ||
                            filter.filterGroup.filters.firstOrNull() !is FilterToggle<*>)
                ) View.VISIBLE
                else View.GONE
        }
    }

    private inner class FilterIntervalNumbersViewHolder(
        private val binding: FilterIntervalNumbersBinding
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

            binding.checkbox.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                val filter = items[pos]
                val state = binding.checkbox.isChecked

                onCheckedChangeListener?.onCheckedChanged(filter, state)
            }

            binding.rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: RangeSlider) {}

                override fun onStopTrackingTouch(rangeSlider: RangeSlider) {
                    val pos = adapterPosition
                    if (pos == RecyclerView.NO_POSITION) {
                        return
                    }

                    val after = rangeSlider.values[0]
                    val before = rangeSlider.values[1]

                    val filter = items[pos]
                    this@FiltersAdapter.onIntervalChangeListener?.onIntervalChange(filter, after, before)
                }
            })
        }

        fun bind(filter: Filter<*>) {
            binding.checkbox.isChecked = filter.enable
            binding.name.text = filter.name

            binding.rangeSlider.valueFrom =
                if (filter.condition is ConditionIntervalNumbers<*>) {
                    filter.condition.intervalBegin.toFloat()
                } else 0F

            binding.rangeSlider.valueTo =
                if (filter.condition is ConditionIntervalNumbers<*>) {
                    filter.condition.intervalEnd.toFloat()
                } else 0F

            if (filter.condition is ConditionIntervalNumbers<*>) {
                  binding.rangeSlider.values = listOf(filter.condition.after.toFloat(),filter.condition.before.toFloat())
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val filter = items[position]
        return when {
            filter.filterGroup.getFiltersByType<FilterToggle<*>>().size in 1..3 -> TYPE_FILTER_TOGGLE
            filter is FilterToggle<*> -> TYPE_FILTER_TOGGLE
            filter.condition is ConditionIntervalNumbers -> TYPE_FILTER_INTERVAL_NUMBERS
            else -> TYPE_FILTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FILTER -> {
                val binding = FilterCheckboxBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                FilterViewHolder(binding)
            }
            else -> {
                val binding = FilterIntervalNumbersBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                FilterIntervalNumbersViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is FilterViewHolder -> holder.bind(item)
            is FilterIntervalNumbersViewHolder -> holder.bind(item)
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

    interface OnIntervalChangeListener {
        fun onIntervalChange(filter: Filter<*>, after: Float, before: Float)
    }
}