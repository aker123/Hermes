package com.example.hermes.domain.filters.conditions

import android.os.Parcelable
import com.example.hermes.domain.filters.Filter
import java.util.*

abstract class ConditionDate<V>(
    open var date: Calendar,
    open var mode: Mode
) : Filter.Condition<V>, Parcelable {

    enum class Mode {
        LESS,
        GREATER,
        EQUALS
    }

    abstract fun getItemDate(item: V): Calendar

    override fun filter(item: V): Boolean {
        val itemDate = getItemDate(item)
        return filter(itemDate)
    }

    fun filter(itemDate: Calendar): Boolean {
        return when (mode) {
            Mode.LESS -> itemDate < date
            Mode.GREATER -> itemDate > date
            Mode.EQUALS -> itemDate == date
        }
    }
}