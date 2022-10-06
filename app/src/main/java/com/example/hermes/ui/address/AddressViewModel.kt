package com.example.hermes.ui.address

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.usecase.delete.DeleteAddressUseCase
import com.example.hermes.domain.usecase.get.GetAddressUseCase
import com.example.hermes.domain.usecase.save.SaveAddressUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddressViewModel(
    application: Application
) : BaseViewModel<AddressContract.Event, AddressContract.State, AddressContract.Effect>(
    application
) {

    @Inject
    lateinit var getAddressUseCase: GetAddressUseCase

    @Inject
    lateinit var deleteAddressUseCase: DeleteAddressUseCase

    @Inject
    lateinit var saveAddressUseCase: SaveAddressUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getAddresses(): List<Address> {
        var addresses: List<Address> = listOf()
        viewModelScope.launch {
            setState { AddressContract.State.Loading }
            try {
                addresses = getAddressUseCase.execute()
            } catch (e: Exception) {
                setEffect { AddressContract.Effect.ShowMessage(R.string.address_error) }
            }
            setState { AddressContract.State.Setting }
        }
        return addresses
    }

    override fun createInitialState(): AddressContract.State {
        return AddressContract.State.Default
    }

    override fun handleEvent(event: AddressContract.Event) {
        when (event) {
            is AddressContract.Event.OnEditingClick -> setEffect { AddressContract.Effect.OnAddressManagerFragmentActivity(event.address, true) }
            is AddressContract.Event.OnCreateAddressClick -> setEffect { AddressContract.Effect.OnAddressManagerFragmentActivity(event.address, false) }
            is AddressContract.Event.OnAddressClick -> setActive(event.address, event.addresses)
            is AddressContract.Event.OnDeleteClick -> delete(event.address, event.addresses)
        }
    }

    private fun delete(address: Address, addresses: MutableList<Address>) {
        viewModelScope.launch {
            setState { AddressContract.State.Loading }
            try {
                deleteAddressUseCase.execute(address)
                addresses.remove(address)
            } catch (e: Exception) {
                setEffect { AddressContract.Effect.ShowMessage(R.string.address_error) }
            }
            setEffect { AddressContract.Effect.Update(addresses) }
            setState { AddressContract.State.Setting }
        }
    }

    private fun setActive(address: Address, addresses: MutableList<Address>) {
        viewModelScope.launch {
            setState { AddressContract.State.Loading }
            addresses.filter { it.active }.forEach { it.active = false }
            address.active = true
            try {
                saveAddressUseCase.execute(addresses)
            } catch (e: Exception) {
                setEffect { AddressContract.Effect.ShowMessage(R.string.address_error) }
            }
            setEffect { AddressContract.Effect.Update() }
            setState { AddressContract.State.Setting }
        }
    }
}