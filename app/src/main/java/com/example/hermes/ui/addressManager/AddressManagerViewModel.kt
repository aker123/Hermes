package com.example.hermes.ui.addressManager

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.usecase.delete.DeleteAddressUseCase
import com.example.hermes.domain.usecase.save.SaveAddressUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddressManagerViewModel(
    application: Application
) : BaseViewModel<AddressManagerContract.Event, AddressManagerContract.State, AddressManagerContract.Effect>(
    application
) {

    @Inject
    lateinit var deleteAddressUseCase: DeleteAddressUseCase

    @Inject
    lateinit var saveAddressUseCase: SaveAddressUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }


    override fun createInitialState(): AddressManagerContract.State {
        return AddressManagerContract.State.Default
    }

    override fun handleEvent(event: AddressManagerContract.Event) {
        when (event) {
            is AddressManagerContract.Event.OnClickSave -> save(event.address)
            is AddressManagerContract.Event.OnClickDelete -> delete(event.address)
        }
    }

    private fun delete(address: Address) {
        viewModelScope.launch {
            setState { AddressManagerContract.State.Loading }
            try {
                deleteAddressUseCase.execute(address)
            } catch (e: Exception) {
                setEffect { AddressManagerContract.Effect.ShowMessage(R.string.address_manager_error) }
            }
            setState { AddressManagerContract.State.Setting }
            setEffect { AddressManagerContract.Effect.OnCallBackDelete(address) }
        }
    }

    private fun save(address: Address) {
        viewModelScope.launch {
            setState { AddressManagerContract.State.Loading }
            try {
                saveAddressUseCase.execute(address)
            } catch (e: Exception) {
                setEffect { AddressManagerContract.Effect.ShowMessage(R.string.address_manager_error) }
            }
            setState { AddressManagerContract.State.Setting }
            setEffect { AddressManagerContract.Effect.OnCallBackAdd(address) }
        }
    }
}