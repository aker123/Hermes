package com.example.hermes.domain.filters

import com.example.hermes.domain.filters.conditions.ConditionDate
import com.example.hermes.domain.filters.conditions.ConditionOrder
import com.example.hermes.domain.models.Order
import java.util.*

object FilterProvider {

    fun getFiltersOrder(orders: List<Order>?): FilterGroup<Order> {
        val filters: MutableList<Filter<Order>> = mutableListOf()

        val statuses = Order.Status.values()
        val methods = Order.Method.values()


        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c.minimalDaysInFirstWeek = 1
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        filters.add(
            Filter(
                ConditionOrder.Date(c, ConditionDate.Mode.EQUALS),
                "Дата",
                false,
                Filter.Mode.AND
            )
        )

        filters.add(
            Filter(
                ConditionOrder.NONE, "Статусы", false, Filter.Mode.AND, FilterGroup(
                    statuses.map { status ->
                        Filter(ConditionOrder.STATUS(status), status.key, false, Filter.Mode.OR)
                    }.toMutableList()
                )
            )
        )

        filters.add(
            Filter(
                ConditionOrder.NONE, "Способы получения", false, Filter.Mode.AND, FilterGroup(
                    methods.map { method ->
                        Filter(ConditionOrder.METHOD(method), method.key, false, Filter.Mode.OR)
                    }.toMutableList()
                )
            )
        )

        if (!orders.isNullOrEmpty()) {
            val priceMax = orders.maxOf { it.amount }
            val priceMin = orders.minOf { it.amount }
            filters.add(
                Filter(
                    ConditionOrder.Price(priceMin, priceMax, 0, priceMax),
                    "Сумма заказа",
                    false,
                    Filter.Mode.AND
                )
            )
        }

        return FilterGroup(filters)
    }
}