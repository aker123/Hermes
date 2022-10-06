package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.repository.ProfileRepository

class GetOperatorByLoginAndPasswordUseCase(
    private val profileRepository: ProfileRepository
) {
    fun execute(login: String, password: String): Operator? {
        return profileRepository.getOperator(login, password)
    }
}