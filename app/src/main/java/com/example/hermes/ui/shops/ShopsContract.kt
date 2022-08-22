package com.example.hermes.ui.shops

import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.models.User
import com.example.hermes.ui.authorization.AuthorizationContract
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class ShopsContract {

    sealed class Event: UiEvent {
        class OnClickShop(
            val shop: Shop
        ) : Event()
        class SaveShopsDB(
            val shops:List<Shop>
        ): Event()
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
        class OnProductsFragmentActivity(
            val shop: Shop
        ): Effect()
    }
}