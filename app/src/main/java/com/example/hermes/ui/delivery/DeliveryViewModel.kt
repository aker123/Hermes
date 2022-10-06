package com.example.hermes.ui.delivery

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.usecase.delete.ClearBasketUseCase
import com.example.hermes.domain.usecase.get.GetAddressActiveUseCase
import com.example.hermes.domain.usecase.send.SendOrderUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.basket.BasketContract
import com.example.hermes.ui.map.MapContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DeliveryViewModel(
    application: Application
) : BaseViewModel<DeliveryContract.Event, DeliveryContract.State, DeliveryContract.Effect>(
    application
) {

    @Inject
    lateinit var sendOrderUseCase: SendOrderUseCase

    @Inject
    lateinit var clearBasketUseCase: ClearBasketUseCase

    @Inject
    lateinit var getAddressActiveUseCase: GetAddressActiveUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    override fun createInitialState(): DeliveryContract.State {
        return DeliveryContract.State.Default
    }

    override fun handleEvent(event: DeliveryContract.Event) {
        when (event) {
            is DeliveryContract.Event.OnClickSendOrder -> clickSendOrder(event.order)
        }
    }

    fun getAddressActive(): Address? {
        var address: Address? = null
        viewModelScope.launch {
            setState { DeliveryContract.State.Loading }
            try {
                address = getAddressActiveUseCase.execute()
            } catch (e: Exception) {
                setEffect { DeliveryContract.Effect.ShowMessage(R.string.delivery_error) }
            }
            setState { DeliveryContract.State.Setting }
        }
        return address
    }

    private fun clickSendOrder(order: Order) {
        viewModelScope.launch {
            setState { DeliveryContract.State.Loading }

            try {
                withContext(Dispatchers.IO) {
                    sendOrderUseCase.execute(order)
                    clearBasketUseCase.execute()
                }
                setEffect { DeliveryContract.Effect.ShowMessage(R.string.delivery_mes_send_success) }
                setEffect { DeliveryContract.Effect.OnGeneralActivity }
            } catch (e: IOException) {
                setEffect { DeliveryContract.Effect.ShowMessage(R.string.delivery_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { DeliveryContract.Effect.ShowMessage(R.string.delivery_mes_server_error) }
            } catch (e: Exception) {
                setEffect { DeliveryContract.Effect.ShowMessage(R.string.delivery_error) }
            }

            setState { DeliveryContract.State.Setting }
        }
    }
}