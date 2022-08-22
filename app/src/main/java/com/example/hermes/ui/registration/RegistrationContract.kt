package com.example.hermes.ui.registration

import com.example.hermes.domain.models.User
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class RegistrationContract {

    sealed class Event: UiEvent  {
        class OnClickRegister(
            val user:  User
        ) : Event()
    }

    sealed class State: UiState {
        object Default : State()
        object Setting : State()
        object Loading : State()
    }


    sealed class Effect: UiEffect  {
        class ShowMessage(
            val messageId: Int
        ) : Effect()

        object OnGeneralActivity : Effect()
    }
}