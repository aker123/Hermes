package com.example.hermes.ui.basket

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.*
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.products.ProductsContract
import com.example.hermes.ui.shops.ShopsContract
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BasketViewModel(
    application: Application
) : BaseViewModel<BasketContract.Event, BasketContract.State, BasketContract.Effect>(
    application
) {
    @Inject
    lateinit var getSelectedProductsUseCase: GetSelectedProductsUseCase

    @Inject
    lateinit var getShopDBUseCase: GetShopDBUseCase

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getSelectedProducts(): List<Product> {
        var products: List<Product> = listOf()
        viewModelScope.launch {
            setState { BasketContract.State.Loading }
            try {
                products = getSelectedProductsUseCase.execute()
            } catch (e: Exception) {
                setEffect { BasketContract.Effect.ShowMessage(R.string.registration_error) }
            }

            setState { BasketContract.State.Setting }
        }
        return products
    }

    override fun createInitialState(): BasketContract.State {
        return BasketContract.State.Default
    }

    override fun handleEvent(event: BasketContract.Event) {
        when (event) {
            is BasketContract.Event.OnClickProduct -> clickProduct(event.product)
            is BasketContract.Event.OnClickAdd -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity + 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { BasketContract.Effect.Update }
                }
            }
            is BasketContract.Event.OnClickRemove -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity - 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { BasketContract.Effect.Update }
                }
            }
            is BasketContract.Event.OnClickDelivery -> {
                viewModelScope.launch {
                    val shop =
                        event.products?.first()?.let { getShopDBUseCase.execute(it.shopUid) }
                            ?: return@launch
                    val user = getUserDBUseCase.execute() ?: return@launch
                    val orderProducts = OrderProducts(shop, user, event.products)
                    setEffect { BasketContract.Effect.OnDeliveryFragmentActivity(orderProducts) }

                }
            }

        }
    }

    private fun clickProduct(product: Product) {

    }

}