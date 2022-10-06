package com.example.hermes.ui.payment

import com.cloudipsp.android.Card
import com.cloudipsp.android.Cloudipsp
import com.example.hermes.domain.models.Order
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class PaymentContract {

    sealed class Event: UiEvent {
        class OnClickSendOrder(
            val order: Order
        ) : Event()

        class OnClickPay(
            val order: com.cloudipsp.android.Order,
            val card: Card,
            val cloudipsp: Cloudipsp
        ) : Event()
    }

    sealed class State : UiState {
        object Default : State()
        object Setting : State()
        object Loading : State()
    }


    sealed class Effect : UiEffect {
        class ShowMessage(
            val messageId: Int
        ) : Effect()

    }
}