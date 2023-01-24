package com.example.hermes.domain.filters.conditions

import android.os.Parcelable
import com.example.hermes.domain.filters.Filter

abstract class ConditionIntervalNumbers<V>(
    open var after: Long,
    open var before: Long,
    open var intervalBegin: Long,
    open var intervalEnd: Long
) : Filter.Condition<V>, Parcelable {

    abstract fun getItemNumber(item: V): Long

    override fun filter(item: V): Boolean {
        val itemNumber = getItemNumber(item)
        return filter(itemNumber)
    }

    fun filter(itemNumber: Long): Boolean {
        return itemNumber in after..before
    }
}