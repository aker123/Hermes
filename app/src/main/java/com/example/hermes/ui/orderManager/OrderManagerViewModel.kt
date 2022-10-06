package com.example.hermes.ui.orderManager

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.usecase.get.GetOrderProductsUseCase
import com.example.hermes.domain.usecase.get.GetPicassoUseCase
import com.example.hermes.domain.usecase.send.SendOrderStatusUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderManagerViewModel(
    application: Application
) : BaseViewModel<OrderManagerContract.Event, OrderManagerContract.State, OrderManagerContract.Effect>(
    application
) {
    @Inject
    lateinit var sendOrderStatusUseCase: SendOrderStatusUseCase

    @Inject
    lateinit var getOrderProductsUseCase: GetOrderProductsUseCase

    @Inject
    lateinit var getPicassoUseCase: GetPicassoUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    fun getOrderProducts(order: Order): MutableLiveData<List<Product>?> {
        var products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        viewModelScope.launch {
            setState { OrderManagerContract.State.Loading }
            try {
                products = getOrderProductsUseCase.execute(order)
            } catch (e: IOException) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_mes_server_error) }
            } catch (e: Exception) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_error) }
            }
            setState { OrderManagerContract.State.Setting }
        }
        return products
    }

    override fun createInitialState(): OrderManagerContract.State {
        return OrderManagerContract.State.Default
    }

    override fun handleEvent(event: OrderManagerContract.Event) {
        when (event) {
            is OrderManagerContract.Event.OnClickSendOrder -> clickSendOrder(event.order)
        }
    }

    private fun clickSendOrder(order: Order) {
        viewModelScope.launch {
            setState { OrderManagerContract.State.Loading }

            try {
                withContext(Dispatchers.IO) { sendOrderStatusUseCase.execute(order) }
                setEffect { OrderManagerContract.Effect.OnOrdersActivity }
            } catch (e: IOException) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_mes_server_error) }
            } catch (e: Exception) {
                setEffect { OrderManagerContract.Effect.ShowMessage(R.string.order_manager_error) }
            }

            setState { OrderManagerContract.State.Setting }
        }
    }

}