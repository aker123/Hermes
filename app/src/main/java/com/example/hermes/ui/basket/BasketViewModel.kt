package com.example.hermes.ui.basket

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.usecase.delete.ClearBasketUseCase
import com.example.hermes.domain.usecase.delete.DeleteProductDBUseCase
import com.example.hermes.domain.usecase.get.GetPicassoUseCase
import com.example.hermes.domain.usecase.get.GetSelectedProductsUseCase
import com.example.hermes.domain.usecase.get.GetShopDBUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.domain.usecase.save.SaveProductsUseCase
import com.example.hermes.domain.usecase.send.SendOrderUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.delivery.DeliveryContract
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    lateinit var clearBasketUseCase: ClearBasketUseCase

    @Inject
    lateinit var getShopDBUseCase: GetShopDBUseCase

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    @Inject
    lateinit var getPicassoUseCase: GetPicassoUseCase

    @Inject
    lateinit var saveProductsUseCase: SaveProductsUseCase

    @Inject
    lateinit var sendOrderUseCase: SendOrderUseCase

    @Inject
    lateinit var deleteProductDBUseCase: DeleteProductDBUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    fun getSelectedProducts(currentProducts: List<Product>){
        var products: List<Product> = listOf()
        var needUpdate = false
        viewModelScope.launch(Dispatchers.IO) {
            setState { BasketContract.State.Loading }
            try {
                products = getSelectedProductsUseCase.execute()
            } catch (e: Exception) {
                setEffect { BasketContract.Effect.ShowMessage(R.string.basket_error) }
            }
            products.forEach { product ->
                if (currentProducts.none { current -> product.uid == current.uid })
                    needUpdate = true
            }
            setEffect { BasketContract.Effect.Update(products, needUpdate) }
            setState { BasketContract.State.Setting }
        }
    }

    override fun createInitialState(): BasketContract.State {
        return BasketContract.State.Default
    }

    override fun handleEvent(event: BasketContract.Event) {
        when (event) {
            is BasketContract.Event.OnClickAdd -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity + 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { BasketContract.Effect.Update() }
                }
            }
            is BasketContract.Event.OnClickRemove -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity - 1
                    event.product.amount = event.product.price * event.product.quantity
                    if (event.product.quantity == 0L) setEffect {
                        BasketContract.Effect.ShowDialogMessage(
                            event.product
                        )
                    }
                    else setEffect { BasketContract.Effect.Update() }
                }
            }
            is BasketContract.Event.OnClickDelivery -> {
                viewModelScope.launch {
                    val shop =
                        event.products?.first()?.let { getShopDBUseCase.execute(it.shopUid) }
                            ?: return@launch
                    saveProductsUseCase.execute(shop, event.products)
                    val user = getUserDBUseCase.execute() ?: return@launch
                    val orderProducts = OrderProducts(shop, user, event.products)
                    setEffect { BasketContract.Effect.OnDeliveryFragmentActivity(orderProducts) }
                }
            }
            is BasketContract.Event.OnClickPickup -> {
                viewModelScope.launch {
                    val shop =
                        event.products?.first()?.let { getShopDBUseCase.execute(it.shopUid) }
                            ?: return@launch
                    saveProductsUseCase.execute(shop, event.products)
                    val user = getUserDBUseCase.execute() ?: return@launch
                    val orderProducts = OrderProducts(shop, user, event.products)
                    setEffect { BasketContract.Effect.OnPickupBottomDialog(orderProducts) }
                }
            }
            is BasketContract.Event.OnCheckedChange -> {
                viewModelScope.launch {
                    event.product.sizes.forEach {
                        it.selected = it.value == event.size
                    }
                }
            }
            is BasketContract.Event.OnClearBasket -> {
                viewModelScope.launch {
                    clearBasketUseCase.execute()
                    setEffect { BasketContract.Effect.Update() }
                }
            }
            is BasketContract.Event.OnClickSendOrder -> clickSendOrder(event.order)
            is BasketContract.Event.RemoveProductBase -> {
                viewModelScope.launch {
                    deleteProductDBUseCase.execute(event.product)
                    setEffect { BasketContract.Effect.Update() }
                }
            }
        }
    }

    private fun clickSendOrder(order: Order) {
        viewModelScope.launch {
            setState { BasketContract.State.Loading }

            try {
                withContext(Dispatchers.IO) {
                    sendOrderUseCase.execute(order)
                    clearBasketUseCase.execute()
                }
                setEffect { BasketContract.Effect.ShowMessage(R.string.basket_mes_send_success) }
                setEffect { BasketContract.Effect.OnGeneralActivity }
            } catch (e: IOException) {
                setEffect { BasketContract.Effect.ShowMessage(R.string.basket_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { BasketContract.Effect.ShowMessage(R.string.basket_mes_server_error) }
            } catch (e: Exception) {
                setEffect { BasketContract.Effect.ShowMessage(R.string.basket_error) }
            }

            setState { BasketContract.State.Setting }
        }
    }

}