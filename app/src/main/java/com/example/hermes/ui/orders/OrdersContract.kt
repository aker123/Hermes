package com.example.hermes.ui.orders

import com.example.hermes.domain.filters.FilterGroup
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.profile.ProfileContract
import com.example.hermes.ui.shops.ShopsContract

class OrdersContract {

    sealed class Event : UiEvent {
        class OnClickOrder(
            val order: Order
        ) : Event()
        class OnSearch(
            val orders:List<Order>,
            val query: String,
            val filters: FilterGroup<Order>?
        ): Event()
        object OnClickExit : Event()
    }

    sealed class State : UiState {
        object Default : State()
        object Setting : State()
        object Loading : State()
    }


    sealed class Effect : UiEffect {
        class ShowMessage<T>(
            val message: T
        ) : Effect()
        class OnOrderManagerFragmentActivity(
            val order: Order
        ) : Effect()
        class Update(
            val orders:List<Order>,
        ): Effect()
        object OnExit : Effect()
    }
}