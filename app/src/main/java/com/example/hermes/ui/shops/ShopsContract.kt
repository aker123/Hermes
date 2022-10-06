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
        class OnSearch(
            val shops:List<Shop>,
            val query: String
        ): Event()
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
        class OnProductsFragmentActivity(
            val shop: Shop
        ): Effect()
        class Update(
            val shops:List<Shop>,
        ): Effect()
    }
}