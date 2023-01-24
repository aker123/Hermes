package com.example.hermes.ui.products

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.delete.ClearBasketUseCase
import com.example.hermes.domain.usecase.get.GetPicassoUseCase
import com.example.hermes.domain.usecase.get.GetProductsUseCase
import com.example.hermes.domain.usecase.get.GetSelectedProductsUseCase
import com.example.hermes.domain.usecase.save.SaveProductUseCase
import com.example.hermes.domain.usecase.save.SaveProductsUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import javax.inject.Inject


class ProductsViewModel(
    application: Application
) : BaseViewModel<ProductsContract.Event, ProductsContract.State, ProductsContract.Effect>(
    application
) {

    @Inject
    lateinit var getSelectedProductsUseCase: GetSelectedProductsUseCase

    @Inject
    lateinit var getProductsUseCase: GetProductsUseCase

    @Inject
    lateinit var saveProductsUseCase: SaveProductsUseCase

    @Inject
    lateinit var getPicassoUseCase: GetPicassoUseCase

    @Inject
    lateinit var clearBasketUseCase: ClearBasketUseCase

    @Inject
    lateinit var saveProductUseCase: SaveProductUseCase


    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    private fun search(products: List<Product>, query: String, textGender: String?, textCategory:String?) {
        viewModelScope.launch {
            var productsResult = products.filter {
                it.name.lowercase().contains(query.lowercase())
                        || it.price.toString().lowercase().contains(query.lowercase())
                        || it.sizes.any { size ->
                    size.value.lowercase().contains(query.lowercase())
                }
            }
            textGender?.let {
                productsResult = productsResult.filter { it.gender == textGender }
            }
            textCategory?.let {
                productsResult = productsResult.filter { it.category.key == textCategory }
            }

            setEffect { ProductsContract.Effect.Update(productsResult) }
        }
    }

    fun getProducts(shop: Shop): MutableLiveData<List<Product>?> {
        var products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        viewModelScope.launch {
            setState { ProductsContract.State.Loading }
            try {
                products = getProductsUseCase.execute(shop)
            } catch (e: IOException) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.products_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.products_mes_server_error) }
            } catch (e: Exception) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.products_error) }
            }

            setState { ProductsContract.State.Setting }
        }
        return products
    }

    fun getSelectedProducts(selectedProduct: MutableList<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                selectedProduct.addAll(getSelectedProductsUseCase.execute())
            } catch (e: Exception) {
                setEffect { ProductsContract.Effect.ShowMessage(R.string.products_error) }
            }
            setEffect { ProductsContract.Effect.UpdateAmount }
        }
    }

    override fun createInitialState(): ProductsContract.State {
        return ProductsContract.State.Default
    }

    override fun handleEvent(event: ProductsContract.Event) {
        when (event) {
            is ProductsContract.Event.OnCLickAddBasket -> {
                viewModelScope.launch {
                    val product = Product(
                        UUID.randomUUID().toString(),
                        event.product.shopUid,
                        event.product.name,
                        event.product.price,
                        event.product.price,
                        1,
                        event.product.description,
                        event.product.gender,
                        event.product.category,
                        event.product.imagePath,
                        event.product.sizes,
                        event.product.uid
                    )

                    try {
                        saveProductUseCase.execute(product)
                        event.selectedProducts.add(product)
                    } catch (e: Exception) {
                        setEffect { ProductsContract.Effect.ShowMessage(R.string.products_error) }
                    }
                    setEffect { ProductsContract.Effect.Update() }
                }
            }
            is ProductsContract.Event.OnCheckedChange -> {
                viewModelScope.launch {
                    event.product.sizes.forEach {
                        it.selected = it.value == event.size
                    }
                    setEffect { ProductsContract.Effect.Update() }
                }
            }
            is ProductsContract.Event.OnClickOnBasket -> setEffect { ProductsContract.Effect.OnBasketFragmentActivity }
            is ProductsContract.Event.OnSearch -> search(event.products, event.query, event.textGender, event.textCategory)
            is ProductsContract.Event.ClearBasket -> {
                clearBasketUseCase.execute()
            }
        }
    }

}