package com.example.hermes.ui.entrance

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.domain.usecase.get.GetOperatorDBUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class EntranceViewModel(
    application: Application
) : BaseViewModel<EntranceContract.Event, EntranceContract.State, EntranceContract.Effect>(
    application
) {

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    @Inject
    lateinit var getOperatorDBUseCase: GetOperatorDBUseCase

    init {
        (application as Hermes).appComponent?.inject(this)
    }

    private fun checkProfile() {
        viewModelScope.launch {
            setState { EntranceContract.State.Loading }
            try {
                val operator = getOperatorDBUseCase.execute()
                val user = getUserDBUseCase.execute()

                if (operator != null) setEffect { EntranceContract.Effect.OnOrdersActivity }
                else if (user != null) setEffect { EntranceContract.Effect.OnGeneralActivity }

            } catch (e: Exception) {
                setEffect { EntranceContract.Effect.ShowMessage(R.string.entrance_error) }
            }
            setState { EntranceContract.State.Setting }
        }
    }

    override fun createInitialState(): EntranceContract.State {
        return EntranceContract.State.Default
    }

    override fun handleEvent(event: EntranceContract.Event) {
        when (event) {
            is EntranceContract.Event.CheckProfile -> checkProfile()
        }
    }

}