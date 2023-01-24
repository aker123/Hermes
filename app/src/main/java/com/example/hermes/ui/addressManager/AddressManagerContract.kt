package com.example.hermes.ui.addressManager

import com.example.hermes.domain.models.Address
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class AddressManagerContract {

    sealed class Event : UiEvent {
        class OnClickDelete(
            val address: Address
        ) : Event()

        class OnClickSave(
            val address: Address
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
        class OnCallBackDelete(
            val address: Address
        ) : Effect()
        class OnCallBackAdd(
            val address: Address
        ) : Effect()
    }
}