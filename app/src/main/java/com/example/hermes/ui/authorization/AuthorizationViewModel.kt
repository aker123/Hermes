package com.example.hermes.ui.authorization

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.get.GetOperatorByLoginAndPasswordUseCase
import com.example.hermes.domain.usecase.get.GetUserByLoginAndPasswordUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthorizationViewModel(
    application: Application
) : BaseViewModel<AuthorizationContract.Event, AuthorizationContract.State, AuthorizationContract.Effect>(
    application
) {

    @Inject
    lateinit var getUserByLoginAndPasswordUseCase: GetUserByLoginAndPasswordUseCase

    @Inject
    lateinit var getOperatorByLoginAndPasswordUseCase: GetOperatorByLoginAndPasswordUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    override fun createInitialState(): AuthorizationContract.State {
        return AuthorizationContract.State.Default
    }

    override fun handleEvent(event: AuthorizationContract.Event) {
        when (event) {
            is AuthorizationContract.Event.OnClickAuthorize -> authorize(event.login,event.password)
        }
    }

    private fun authorize(login: String, password: String) {
        viewModelScope.launch {
            var user: User?
            var operator: Operator?
            setState { AuthorizationContract.State.Loading }

            try {
                withContext(Dispatchers.IO) {
                    operator = getOperatorByLoginAndPasswordUseCase.execute(login,password)
                    user = getUserByLoginAndPasswordUseCase.execute(login,password)
                }
                if (operator != null) setEffect { AuthorizationContract.Effect.OnOrdersActivity }
                else if (user != null) setEffect { AuthorizationContract.Effect.OnGeneralActivity }
                else setEffect { AuthorizationContract.Effect.ShowMessage(R.string.authorization_error_not_user) }
            } catch (e: IOException) {
                setEffect { AuthorizationContract.Effect.ShowMessage(R.string.authorization_error_not_connection) }
            } catch (e: HttpException) {
                setEffect { AuthorizationContract.Effect.ShowMessage(R.string.authorization_mes_server_error) }
            } catch (e: Exception) {
                setEffect { AuthorizationContract.Effect.ShowMessage(R.string.authorization_error) }
            }

            setState { AuthorizationContract.State.Setting }
        }
    }
}