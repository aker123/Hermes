package com.example.hermes.ui.products

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.GetProductsUseCase
import com.example.hermes.domain.usecase.SaveProductsUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class ProductsViewModel(
    application: Application
) : BaseViewModel<ProductsContract.Event, ProductsContract.State, ProductsContract.Effect>(
    application
) {

    @Inject
    lateinit var getProductsUseCase: GetProductsUseCase

    @Inject
    lateinit var saveProductsUseCase: SaveProductsUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getProducts(shop: Shop): MutableLiveData<List<Product>?> {
        var products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        viewModelScope.launch {
            setState { ProductsContract.State.Loading }
            try {
                products = getProductsUseCase.execute(shop)
            } catch (e: IOException) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.registration_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.registration_mes_server_error) }
            } catch (e: Exception) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.registration_error) }
            }

            setState { ProductsContract.State.Setting }
        }
        return products
    }

    override fun createInitialState(): ProductsContract.State {
        return ProductsContract.State.Default
    }

    override fun handleEvent(event: ProductsContract.Event) {
        when (event) {
            is ProductsContract.Event.OnClickProduct -> clickProduct(event.product)
            is ProductsContract.Event.OnCLickPrice -> {
                viewModelScope.launch {
                    event.product.quantity = 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { ProductsContract.Effect.Update }
                }
            }
            is ProductsContract.Event.OnClickAdd -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity + 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { ProductsContract.Effect.Update }
                }
            }
            is ProductsContract.Event.OnClickRemove -> {
                viewModelScope.launch {
                    event.product.quantity = event.product.quantity - 1
                    event.product.amount = event.product.price * event.product.quantity
                    setEffect { ProductsContract.Effect.Update }
                }
            }
            is ProductsContract.Event.OnClickOnBasket -> onBasket(event.shop,event.products)
        }
    }

    private fun onBasket(shop: Shop?,products: List<Product>?) {
        if (products != null && shop != null) {
            saveProductsUseCase.execute(
                shop,
                products.filter {
                    it.quantity > 0
                }
            ).apply {
                setEffect { ProductsContract.Effect.OnBasketFragmentActivity }
            }
        }
    }

    private fun clickProduct(product: Product) {

    }

}