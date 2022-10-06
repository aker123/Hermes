package com.example.hermes.ui.profile

import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class ProfileContract {

    sealed class Event: UiEvent {
        object OnClickOrderHistory : Event()
        object OnClickAddress : Event()
        object OnClickData : Event()
        object OnClickExit : Event()
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
        object OnOrderHistoryFragmentActivity : Effect()
        object OnAddressFragmentActivity : Effect()
        object OnDataFragmentActivity : Effect()
        object OnExit : Effect()
    }
}