package com.example.hermes.ui.shops

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.usecase.get.GetPicassoUseCase
import com.example.hermes.domain.usecase.get.GetShopsUseCase
import com.example.hermes.domain.usecase.save.SaveShopsDBUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var getPicassoUseCase: GetPicassoUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getPicasso(): Picasso {
        return getPicassoUseCase.execute()
    }

    private fun search(shops: List<Shop>, query: String) {
        viewModelScope.launch {
            val shopsResult = shops.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            setEffect { ShopsContract.Effect.Update(shopsResult) }
        }
    }

    fun getShops(): MutableLiveData<List<Shop>?> {
        var shops: MutableLiveData<List<Shop>?> = MutableLiveData<List<Shop>?>()
        viewModelScope.launch {
            try {
                shops = getShopUseCase.execute()
            } catch (e: IOException) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.shops_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.shops_mes_server_error) }
            } catch (e: Exception) {
                setEffect { ShopsContract.Effect.ShowMessage(R.string.shops_error) }
            }
        }
        return shops
    }

    override fun createInitialState(): ShopsContract.State {
        return ShopsContract.State.Default
    }

    override fun handleEvent(event: ShopsContract.Event) {
        when (event) {
            is ShopsContract.Event.OnClickShop -> setEffect {
                ShopsContract.Effect.OnProductsFragmentActivity(
                    event.shop
                )
            }
            is ShopsContract.Event.SaveShopsDB -> saveShops(event.shops)
            is ShopsContract.Event.OnSearch -> search(event.shops, event.query)
        }
    }

    private fun saveShops(shops: List<Shop>) {
        viewModelScope.launch {
            saveShopsDBUseCase.execute(shops)
        }
    }
}