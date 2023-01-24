package com.example.hermes.ui.orderHistory

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.get.*
import com.example.hermes.domain.usecase.save.UpdateOrdersUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.orders.OrdersContract
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderHistoryViewModel(
    application: Application
) : BaseViewModel<OrderHistoryContract.Event, OrderHistoryContract.State, OrderHistoryContract.Effect>(
    application
) {

    @Inject
    lateinit var getPicassoUseCase: GetPicassoUseCase

    @Inject
    lateinit var getOrderHistoryUseCase: GetOrderHistoryUseCase

    @Inject
    lateinit var getActiveOrdersUseCase: GetActiveOrdersUseCase

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    @Inject
    lateinit var getOrdersDBUseCase: GetOrdersDBUseCase

    @Inject
    lateinit var updateOrdersUseCase: UpdateOrdersUseCase

    @Inject
    lateinit var getOrdersHeaderUseCase: GetOrdersHeaderUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getUserDB(): User? {
        var user: User? = null
        viewModelScope.launch {
            try {
                user = getUserDBUseCase.execute()
            } catch (e: Exception) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_error) }
            }
        }
        return user
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    fun getOrderHistory(user: User, onlyActiveOrders: Boolean): MutableLiveData<List<Order>?> {
        var orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        viewModelScope.launch {
            try {
                orders = if (onlyActiveOrders) getActiveOrdersUseCase.execute(user)
                else getOrderHistoryUseCase.execute(user)
            } catch (e: IOException) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_mes_server_error) }
            } catch (e: Exception) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_error) }
            }
        }
        return orders
    }

    fun startNotification() {
        var ordersDB: List<Order>
        var orders: List<Order>
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = getUserDBUseCase.execute() ?: return@launch
                orders = getOrdersHeaderUseCase.execute(user)
                ordersDB = getOrdersDBUseCase.execute()
                setEffect {
                    OrderHistoryContract.Effect.ShowNotificationsOrdersDif(
                        getOrdersDif(
                            ordersDB,
                            orders
                        )
                    )
                }
            } catch (e: Exception) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_error) }
            }
        }
    }

    fun updateOrdersDB(orders: List<Order>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateOrdersUseCase.execute(orders)
            } catch (e: Exception) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.order_history_error) }
            }
        }
    }

    private fun getOrdersDif(ordersDB: List<Order>, orders: List<Order>): List<Order> {
        val ordersDif: MutableList<Order> = mutableListOf()
        ordersDB.forEach { orderDB ->
            orders.forEach { order ->
                if (orderDB.uid == order.uid) {
                    if (orderDB.status != order.status)
                        ordersDif.add(order)
                }
            }
        }

        return ordersDif
    }

    override fun createInitialState(): OrderHistoryContract.State {
        return OrderHistoryContract.State.Default
    }

    override fun handleEvent(event: OrderHistoryContract.Event) {}
}