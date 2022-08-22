package com.example.hermes.ui.basket

import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.products.ProductsContract

class BasketContract {

    sealed class Event: UiEvent {
        class OnClickProduct(
            val product: Product
        ) : Event()
        class OnClickAdd(
            val product: Product
        ): Event()
        class OnClickRemove(
            val product: Product
        ): Event()
        class OnClickDelivery(
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
        class OnDeliveryFragmentActivity(
            val orderProducts: OrderProducts
        ): Effect()
    }
}