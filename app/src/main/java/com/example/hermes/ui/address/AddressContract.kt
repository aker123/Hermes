package com.example.hermes.ui.address
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.base.UiEffect
import com.example.hermes.ui.base.UiEvent
import com.example.hermes.ui.base.UiState
import com.example.hermes.ui.basket.BasketContract

class AddressContract {

    sealed class Event: UiEvent {
        class OnEditingClick(
            val address: Address
        ) : Event()
        class OnCreateAddressClick(
            val address: Address
        ) : Event()
        class OnDeleteClick(
            val address: Address,
            val addresses: MutableList<Address>
        ) : Event()
        class OnAddressClick(
            val address: Address,
            val addresses: MutableList<Address>
        ) : Event()
    }

    sealed class State: UiState {
        object Default : State()
        object Setting : State()
        object Loading : State()
    }

    sealed class Effect: UiEffect {
        class ShowMessage<T>(
            val message: T
        ) : Effect()
        class Update(
            val addresses: MutableList<Address>? = null
        ): Effect()
        class OnAddressManagerFragmentActivity(
            val address: Address,
            val isVisibleDelete: Boolean
        ) : Effect()
    }
}