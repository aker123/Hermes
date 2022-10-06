package com.example.hermes.ui.orderHistory

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.get.GetActiveOrdersUseCase
import com.example.hermes.domain.usecase.get.GetOrderHistoryUseCase
import com.example.hermes.domain.usecase.get.GetPicassoUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
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

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getUserDB(): User? {
        return getUserDBUseCase.execute()
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    fun getOrderHistory(user: User, onlyActiveOrders:Boolean): MutableLiveData<List<Order>?> {
        var orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        viewModelScope.launch {
            setState { OrderHistoryContract.State.Loading }
            try {
                orders = if (onlyActiveOrders) getActiveOrdersUseCase.execute(user)
                else getOrderHistoryUseCase.execute(user)
            } catch (e: IOException) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.registration_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.registration_mes_server_error) }
            } catch (e: Exception) {
                setEffect { OrderHistoryContract.Effect.ShowMessage(R.string.registration_error) }
            }

            setState { OrderHistoryContract.State.Setting }
        }
        return orders
    }

    override fun createInitialState(): OrderHistoryContract.State { return OrderHistoryContract.State.Default }

    override fun handleEvent(event: OrderHistoryContract.Event) {}
}