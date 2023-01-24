package com.example.hermes.ui.basket

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState

class BasketContract {

    sealed class Event: UiEvent {
        class OnClickAdd(
            val product: Product
        ): Event()
        class OnClickRemove(
            val product: Product
        ): Event()
        class RemoveProductBase(
            val product: Product
        ): Event()
        class OnClickDelivery(
            val products: List<Product>?
        ): Event()
        class OnClickPickup(
            val products: List<Product>?
        ): Event()
        class OnCheckedChange(
            val product: Product,
            val size: String
        ): Event()
        object OnClearBasket: Event()
        class OnClickSendOrder(
            val order: Order
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
        class ShowDialogMessage(
            val product: Product
        ) : Effect()
        class Update(
            val products: List<Product>? = null,
            val isNeedUpdate: Boolean? = null
        ): Effect()
        class OnDeliveryFragmentActivity(
            val orderProducts: OrderProducts
        ): Effect()
        class OnPickupBottomDialog(
            val orderProducts: OrderProducts
        ): Effect()
        object OnGeneralActivity: Effect()
    }
}