package com.example.hermes.domain.usecase

import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.repository.EntranceRepository

class GetOperatorDBUseCase(
    private val entranceRepository: EntranceRepository
) {

    fun execute(): Operator? {
        return entranceRepository.getOperatorDB()
    }
}