package com.example.hermes.domain.usecase

import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.AuthorizationRepository

class GetUserByLoginAndPasswordUseCase(
    private val authorizationRepository: AuthorizationRepository
) {
    fun execute(login: String, password: String): User? {
        return authorizationRepository.getUser(login, password)
    }
}