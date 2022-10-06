package com.example.hermes.ui.payment

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.cloudipsp.android.Card
import com.cloudipsp.android.Cloudipsp
import com.cloudipsp.android.Cloudipsp.Exception.*
import com.cloudipsp.android.Cloudipsp.PayCallback
import com.cloudipsp.android.Receipt
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.usecase.send.SendOrderUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PaymentViewModel(
    application: Application
) : BaseViewModel<PaymentContract.Event, PaymentContract.State, PaymentContract.Effect>(
    application
), PayCallback {

    @Inject
    lateinit var sendOrderUseCase: SendOrderUseCase


    init {
        (application as Hermes).appComponent?.inject(this)
    }


    override fun createInitialState(): PaymentContract.State {
        return PaymentContract.State.Default
    }

    override fun handleEvent(event: PaymentContract.Event) {
        when (event) {
            is PaymentContract.Event.OnClickSendOrder -> clickSendOrder(event.order)
            is PaymentContract.Event.OnClickPay -> clickPay(event.order,event.card,event.cloudipsp)
        }
    }

    private fun clickPay(order: com.cloudipsp.android.Order, card: Card, cloudipsp: Cloudipsp) {
        cloudipsp.pay(card, order, this)
    }

    private fun clickSendOrder(order: Order) {
        viewModelScope.launch {
            setState { PaymentContract.State.Loading }

            try {
                withContext(Dispatchers.IO) { sendOrderUseCase.execute(order) }
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_mes_send_success) }
            } catch (e: IOException) {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_mes_server_error) }
            } catch (e: Exception) {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error) }
            }

            setState { PaymentContract.State.Setting }
        }
    }

    override fun onPaidProcessed(receipt: Receipt?) {
        viewModelScope.launch {
            setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_mes_send_success) }
        }
    }

    override fun onPaidFailure(e: Cloudipsp.Exception?) {
        when (e) {
            is Failure -> {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error) }
            }
            is NetworkSecurity -> {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error_not_connection) }
            }
            is ServerInternalError -> {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error) }
            }
            is NetworkAccess -> {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error) }
            }
            else -> {
                setEffect { PaymentContract.Effect.ShowMessage(R.string.delivery_error) }
            }
        }
    }
}