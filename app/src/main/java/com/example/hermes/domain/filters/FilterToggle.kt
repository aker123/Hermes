package com.example.hermes.domain.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterToggle<V>(
    override val name: String,
    override var enable: Boolean,
    override var mode: Mode,
    override val filterGroup: FilterGroup<V>
) : Filter<V>(Condition<V>(), name, enable, mode, filterGroup), Parcelable {

    class Condition<V> : Filter.Condition<V> {
        override fun filter(item: V) = true
    }

    val currentToggle: Filter<V>?
        get() = filterGroup.filters.firstOrNull { it.enable }

    override fun filterGroup(item: V): Boolean {
        var complete = true
        currentToggle?.let {
            complete = complete && it.filter(item)
        }
        return complete
    }
}