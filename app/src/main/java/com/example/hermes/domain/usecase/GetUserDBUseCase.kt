package com.example.hermes.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.RegistrationRepository

class GetUserDBUseCase(
    private val registrationRepository: RegistrationRepository
) {

    fun execute(): User?{
        return registrationRepository.getUserDB()
    }
}