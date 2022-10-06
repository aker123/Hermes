package com.example.hermes.ui.delivery

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.basket.BasketContract
import com.example.hermes.ui.orders.OrdersContract

class DeliveryContract {

    sealed class Event: UiEvent {
        class OnClickSendOrder(
            val order: Order
        ) : Event()
    }

    sealed class State: UiState {
        object Default : State()
        object Setting : State()
        object Loading : State()
    }


    sealed class Effect: UiEffect {
        class ShowMessage<T>(
            val message: T
        ) : Effect()
        object OnGeneralActivity: DeliveryContract.Effect()
    }
}