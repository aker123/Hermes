package com.example.hermes.ui.map

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.usecase.delete.DeleteUserDBUseCase
import com.example.hermes.domain.usecase.get.GetAddressUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.basket.BasketContract
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel(
    application: Application
) : BaseViewModel<MapContract.Event, MapContract.State, MapContract.Effect>(
    application
) {

    @Inject
    lateinit var getAddressUseCase: GetAddressUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getAddress(): List<Address> {
        var address: List<Address> = listOf()
        viewModelScope.launch {
            setState { MapContract.State.Loading }
            try {
                address = getAddressUseCase.execute()
            } catch (e: Exception) {
                setEffect { MapContract.Effect.ShowMessage(R.string.map_error) }
            }
            setState { MapContract.State.Setting }
        }
        return address
    }

    override fun createInitialState(): MapContract.State {
        return MapContract.State.Default
    }

    override fun handleEvent(event: MapContract.Event) {}
}