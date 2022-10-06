package com.example.hermes.domain.usecase.delete

import com.example.hermes.domain.repository.ProfileRepository

class DeleteOperatorDBUseCase(
    private val profileRepository: ProfileRepository
) {

    fun execute(){
        profileRepository.deleteOperatorDB()
    }
}