package com.example.hermes.ui.profileManager

import com.example.hermes.domain.models.User
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class ProfileManagerContract {

    sealed class Event: UiEvent {
        class OnClickSave(
            val user: User
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
        class OnExit(
            val user: User
        ) : Effect()
    }
}