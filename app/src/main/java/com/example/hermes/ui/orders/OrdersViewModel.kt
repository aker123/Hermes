package com.example.hermes.ui.orders

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.filters.FilterGroup
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.delete.DeleteOperatorDBUseCase
import com.example.hermes.domain.usecase.delete.DeleteUserDBUseCase
import com.example.hermes.domain.usecase.get.GetOrdersUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.profile.ProfileContract
import com.example.hermes.ui.shops.ShopsContract
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrdersViewModel(
    application: Application
) : BaseViewModel<OrdersContract.Event, OrdersContract.State, OrdersContract.Effect>(
    application
) {

    @Inject
    lateinit var deleteOperatorDBUseCase: DeleteOperatorDBUseCase

    @Inject
    lateinit var getOrdersUseCase: GetOrdersUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    private fun search(orders: List<Order>, query: String, filters: FilterGroup<Order>?) {
        viewModelScope.launch {
            val shopsResult = orders.filter {
                it.number.lowercase().contains(query.lowercase())
                        || it.status.key.lowercase().contains(query.lowercase())
                        || it.client.name.lowercase().contains(query.lowercase())
                        || it.client.surname.lowercase().contains(query.lowercase())
            }
                .filter {
                    filters?.filter(it) == true
                }
            setEffect { OrdersContract.Effect.Update(shopsResult) }
        }
    }

    fun getOrders(shop: Shop): MutableLiveData<List<Order>?> {
        var orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        viewModelScope.launch {
            setState { OrdersContract.State.Loading }
            try {
                orders = getOrdersUseCase.execute(shop)
            } catch (e: IOException) {
                setEffect { OrdersContract.Effect.ShowMessage(R.string.orders_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { OrdersContract.Effect.ShowMessage(R.string.orders_mes_server_error) }
            } catch (e: Exception) {
                setEffect { OrdersContract.Effect.ShowMessage(R.string.orders_error) }
            }

            setState { OrdersContract.State.Setting }
        }
        return orders
    }

    override fun createInitialState(): OrdersContract.State {
        return OrdersContract.State.Default
    }

    override fun handleEvent(event: OrdersContract.Event) {
        when (event) {
            is OrdersContract.Event.OnClickOrder -> setEffect {
                OrdersContract.Effect.OnOrderManagerFragmentActivity(
                    event.order
                )
            }
            is OrdersContract.Event.OnSearch -> search(event.orders, event.query,event.filters)
            OrdersContract.Event.OnClickExit -> {
                viewModelScope.launch {
                    deleteOperatorDBUseCase.execute()
                    setEffect { OrdersContract.Effect.OnExit }
                }
            }
        }
    }
}