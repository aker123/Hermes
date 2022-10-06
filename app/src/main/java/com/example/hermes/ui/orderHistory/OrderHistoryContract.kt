package com.example.hermes.ui.orderHistory

import com.example.hermes.domain.models.Order
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class OrderHistoryContract {

    sealed class Event : UiEvent

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