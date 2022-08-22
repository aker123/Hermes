package com.example.hermes.ui.products

import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.shops.ShopsContract

class ProductsContract {

    sealed class Event: UiEvent {
        class OnClickProduct(
            val product: Product
        ) : Event()
        class OnCLickPrice(
            val product: Product
        ): Event()
        class OnClickAdd(
            val product: Product
        ): Event()
        class OnClickRemove(
            val product: Product
        ): Event()
        class OnClickOnBasket(
            val shop: Shop?,
            val products: List<Product>?
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
        object Update : Effect()
        object OnBasketFragmentActivity : Effect()
    }
}