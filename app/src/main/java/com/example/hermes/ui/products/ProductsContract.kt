package com.example.hermes.ui.products

import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.basket.BasketContract
import com.example.hermes.ui.shops.ShopsContract

class ProductsContract {

    sealed class Event: UiEvent {
        class OnCLickAddBasket(
            val product: Product,
            val selectedProducts: MutableList<Product>
        ): Event()
        object OnClickOnBasket: Event()
        class OnSearch(
            val products: List<Product>,
            val query: String,
            val textGender: String?,
            val textCategory:String?,
        ): Event()
        class OnCheckedChange(
            val product: Product,
            val size: String
        ): Event()
        object ClearBasket: Event()
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
        class Update(
            val products: List<Product>? = null
        ): Effect()
        object OnBasketFragmentActivity : Effect()
        object UpdateAmount : Effect()
    }
}