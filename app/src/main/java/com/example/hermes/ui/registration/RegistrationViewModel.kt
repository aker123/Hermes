package com.example.hermes.ui.registration

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.application.Hermes
import com.example.hermes.R
import com.example.hermes.domain.data.network.utils.ApiErrorException
import com.example.hermes.ui.base.BaseViewModel
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.save.SaveUserUseCase
import com.yandex.runtime.network.BadRequestError
import com.yandex.runtime.network.internal.BadRequestErrorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class RegistrationViewModel(
    application: Application
) : BaseViewModel<RegistrationContract.Event, RegistrationContract.State, RegistrationContract.Effect>(
    application
) {

    @Inject
    lateinit var saveUserUseCase: SaveUserUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    override fun createInitialState(): RegistrationContract.State {
        return RegistrationContract.State.Default
    }

    override fun handleEvent(event: RegistrationContract.Event) {
        when (event) {
            is RegistrationContract.Event.OnClickRegister -> register(event.user)
        }
    }

    private fun register(user: User) {
        viewModelScope.launch {
            setState { RegistrationContract.State.Loading }

            try {
                withContext(Dispatchers.IO) { saveUserUseCase.execute(user) }
                setEffect { RegistrationContract.Effect.OnGeneralActivity }
            } catch (e: IOException) {
                setEffect { RegistrationContract.Effect.ShowMessage(R.string.registration_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { RegistrationContract.Effect.ShowMessage(R.string.registration_mes_server_error) }
            } catch (e: ApiErrorException) {
                e.message?.let {
                    setEffect { RegistrationContract.Effect.ShowMessage(it) }
                }
            } catch (e: Exception) {
                setEffect { RegistrationContract.Effect.ShowMessage(R.string.registration_error) }
            }

            setState { RegistrationContract.State.Setting }
        }
    }

}