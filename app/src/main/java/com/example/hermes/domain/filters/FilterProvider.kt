package com.example.hermes.domain.filters

import com.example.hermes.domain.models.Order

object FilterProvider {

    fun getFiltersOrder(): FilterGroup<Order> {
        val filters: MutableList<Filter<Order>> = mutableListOf()

        val statuses = Order.Status.values()

        filters.add(
            Filter(ConditionOrder.NONE, "Статусы", false, Filter.Mode.AND, FilterGroup(
                statuses.map { status ->
                    Filter(ConditionOrder.STATUS(status), status.key, false, Filter.Mode.OR)
                }.toMutableList()))
        )

        return FilterGroup(filters)
    }
}