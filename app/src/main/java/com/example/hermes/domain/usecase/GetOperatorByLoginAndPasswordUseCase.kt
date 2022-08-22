package com.example.hermes.domain.usecase

import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.AuthorizationRepository

class GetOperatorByLoginAndPasswordUseCase(
    private val authorizationRepository: AuthorizationRepository
) {
    fun execute(login: String, password: String): Operator? {
        return authorizationRepository.getOperator(login, password)
    }
}