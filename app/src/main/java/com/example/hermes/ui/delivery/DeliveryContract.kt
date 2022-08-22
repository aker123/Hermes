package com.example.hermes.ui.delivery

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

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
        class ShowMessage(
            val messageId: Int
        ) : Effect()
    }
}