package com.example.hermes.ui.authorization

import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.User
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.registration.RegistrationContract

class AuthorizationContract {

    sealed class Event: UiEvent {
        class OnClickAuthorize(
            val login: String,
            val password: String
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
        object OnGeneralActivity : Effect()
        object OnOrdersActivity : Effect()
    }
}