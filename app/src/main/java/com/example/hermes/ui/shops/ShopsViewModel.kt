package com.example.hermes.ui.shops

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.GetProductsUseCase
import com.example.hermes.domain.usecase.GetShopsUseCase
import com.example.hermes.domain.usecase.SaveShopsDBUseCase
import com.example.hermes.ui.authorization.AuthorizationContract
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ShopsViewModel(
    application: Application
) : BaseViewModel<ShopsContract.Event, ShopsContract.State, ShopsContract.Effect>(
    application
) {
    @Inject
    lateinit var getShopUseCase: GetShopsUseCase

    @Inject
    lateinit var saveShopsDBUseCase: SaveShopsDBUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getShops(): MutableLiveData<List<Shop>?> {
        var shops: MutableLiveData<List<Shop>?>  = MutableLiveData<List<Shop>?>()
        viewModelScope.launch {
            setState { ShopsContract.State.Loading }
            try {
                 shops = getShopUseCase.execute()
            } catch (e: IOException) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.registration_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.registration_mes_server_error) }
            } catch (e: Exception) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.registration_error) }
            }

            setState { ShopsContract.State.Setting }
        }
        return shops
    }

    override fun createInitialState(): ShopsContract.State {
        return ShopsContract.State.Default
    }

    override fun handleEvent(event: ShopsContract.Event) {
        when (event) {
            is ShopsContract.Event.OnClickShop -> setEffect { ShopsContract.Effect.OnProductsFragmentActivity(event.shop) }
            is ShopsContract.Event.SaveShopsDB -> saveShops(event.shops)
        }
    }

    private fun saveShops(shops: List<Shop>) {
        viewModelScope.launch {
            saveShopsDBUseCase.execute(shops)
        }
    }
}