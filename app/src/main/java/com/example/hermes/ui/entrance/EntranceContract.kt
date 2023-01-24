package com.example.hermes.ui.entrance
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class EntranceContract {

    sealed class Event: UiEvent {
        object CheckProfile: Event()
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
        object OnOrdersActivity: Effect()
        object OnGeneralActivity: Effect()
    }
}