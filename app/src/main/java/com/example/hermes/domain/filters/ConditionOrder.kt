package com.example.hermes.domain.filters

import com.example.hermes.domain.models.Order

sealed class ConditionOrder: Filter.Condition<Order> {
    object NONE : ConditionOrder() {
        override fun filter(item: Order) = true
    }

    class STATUS(private val status: Order.Status) : ConditionOrder() {
        override fun filter(item: Order): Boolean {
            return status == item.status
        }
    }

}