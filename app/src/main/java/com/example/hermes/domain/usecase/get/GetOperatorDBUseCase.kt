package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.repository.ProfileRepository

class GetOperatorDBUseCase(
    private val profileRepository: ProfileRepository
) {

    fun execute(): Operator? {
        return profileRepository.getOperatorDB()
    }
}