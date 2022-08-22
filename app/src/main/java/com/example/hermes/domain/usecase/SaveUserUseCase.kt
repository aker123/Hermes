package com.example.hermes.domain.usecase


import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.RegistrationRepository

class SaveUserUseCase(
    private val registrationRepository: RegistrationRepository
) {

    fun execute(user: User) {
        return registrationRepository.saveUser(user)
    }
}