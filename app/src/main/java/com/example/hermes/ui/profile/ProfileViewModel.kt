package com.example.hermes.ui.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.delete.DeleteUserDBUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.ui.basket.BasketContract
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel(
    application: Application
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>(
    application
) {

    @Inject
    lateinit var deleteUserDBUseCase: DeleteUserDBUseCase

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    fun getUser(): User? {
        var user: User? = null
        viewModelScope.launch {
            setState { ProfileContract.State.Loading }
            try {
                user = getUserDBUseCase.execute()
            } catch (e: Exception) {
                setEffect { ProfileContract.Effect.ShowMessage(R.string.basket_error) }
            }
            setEffect { ProfileContract.Effect.Update }
            setState { ProfileContract.State.Setting }
        }
        return user
    }


    override fun createInitialState(): ProfileContract.State {
        return ProfileContract.State.Default
    }

    override fun handleEvent(event: ProfileContract.Event) {
        when (event) {
            ProfileContract.Event.OnClickExit -> {
                viewModelScope.launch {
                    deleteUserDBUseCase.execute()
                    setEffect { ProfileContract.Effect.OnExit }
                }
            }
            ProfileContract.Event.OnClickOrderHistory -> setEffect { ProfileContract.Effect.OnOrderHistoryFragmentActivity }
            ProfileContract.Event.OnClickAddress -> setEffect { ProfileContract.Effect.OnAddressFragmentActivity }
            ProfileContract.Event.OnClickData -> setEffect { ProfileContract.Effect.OnDataFragmentActivity }
        }
    }
}