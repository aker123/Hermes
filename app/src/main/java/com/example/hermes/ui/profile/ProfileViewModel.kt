package com.example.hermes.ui.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.delete.DeleteUserDBUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.ui.base.BaseViewModel
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

    fun getUser(): User?{
       return getUserDBUseCase.execute()
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
            ProfileContract.Event.OnClickOrderHistory -> setEffect { ProfileContract.Effect.OnOrderHistoryFragmentActivity}
            ProfileContract.Event.OnClickAddress -> setEffect { ProfileContract.Effect.OnAddressFragmentActivity}
            ProfileContract.Event.OnClickData -> setEffect { ProfileContract.Effect.OnDataFragmentActivity}
        }
    }
}