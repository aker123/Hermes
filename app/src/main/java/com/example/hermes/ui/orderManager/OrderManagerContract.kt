package com.example.hermes.ui.orderManager

import com.example.hermes.domain.models.Order
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.orders.OrdersContract

class OrderManagerContract {

    sealed class Event : UiEvent {
        class OnClickSendOrder(
            val order: Order
        ) : Event()
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
        object OnOrdersActivity: Effect()
    }
}