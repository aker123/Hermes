package com.example.hermes.domain.filters.conditions

import android.os.Parcelable
import com.example.hermes.domain.filters.Filter
import com.example.hermes.domain.models.Order
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class ConditionOrder : Filter.Condition<Order> {
    object NONE : ConditionOrder() {
        override fun filter(item: Order) = true
    }

    class STATUS(private val status: Order.Status) : ConditionOrder() {
        override fun filter(item: Order): Boolean {
            return status == item.status
        }
    }

    class METHOD(private val method: Order.Method) : ConditionOrder() {
        override fun filter(item: Order): Boolean {
            return method == item.method
        }
    }

    @Parcelize
    class Date(
        override var date: Calendar,
        override var mode: Mode
    ) : ConditionDate<Order>(date, mode), Parcelable {

        override fun getItemDate(item: Order): Calendar {
            val c: Calendar = Calendar.getInstance()
            c.firstDayOfWeek = Calendar.MONDAY
            c.minimalDaysInFirstWeek = 1
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            var s = item.date
            s = s.replace(" ", "")
            s = s.replace(".", "")
            s = s.replace("-", "")
            s = s.replace(":", "")
            val d = s.substring(0, 2).toInt()
            val m = s.substring(2, 4).toInt()
            val y = s.substring(4, 8).toInt()
            c.set(y, m - 1, d)
            return c
        }
    }

    @Parcelize
    class Price(
        override var after: Long = 0,
        override var before: Long = 0,
        override var intervalBegin: Long = 0,
        override var intervalEnd: Long = 0
    ) : ConditionIntervalNumbers<Order>(after, before,intervalBegin,intervalEnd), Parcelable {

        override fun getItemNumber(item: Order): Long {
           return item.amount
        }
    }

}