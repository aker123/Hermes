package com.example.hermes.ui.profileManager

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.data.network.utils.ApiErrorException
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.save.SaveUserUseCase
import com.example.hermes.domain.usecase.save.UpdateUserUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProfileManagerViewModel(
    application: Application
) : BaseViewModel<ProfileManagerContract.Event, ProfileManagerContract.State, ProfileManagerContract.Effect>(
    application
) {

    @Inject
    lateinit var updateUserUseCase: UpdateUserUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    override fun createInitialState(): ProfileManagerContract.State {
        return ProfileManagerContract.State.Default
    }

    override fun handleEvent(event: ProfileManagerContract.Event) {
        when (event) {
            is ProfileManagerContract.Event.OnClickSave -> save(event.user)
        }
    }

    private fun save(user: User) {
        viewModelScope.launch {
            setState { ProfileManagerContract.State.Loading }

            try {
                withContext(Dispatchers.IO) { updateUserUseCase.execute(user) }
                setEffect { ProfileManagerContract.Effect.OnExit(user) }
            } catch (e: IOException) {
                setEffect { ProfileManagerContract.Effect.ShowMessage(R.string.profile_manager_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { ProfileManagerContract.Effect.ShowMessage(R.string.profile_manager_mes_server_error) }
            } catch (e: ApiErrorException) {
                e.message?.let {
                    setEffect { ProfileManagerContract.Effect.ShowMessage(it) }
                }
            } catch (e: Exception) {
                setEffect { ProfileManagerContract.Effect.ShowMessage(R.string.profile_manager_error) }
            }

            setState { ProfileManagerContract.State.Setting }
        }
    }

}