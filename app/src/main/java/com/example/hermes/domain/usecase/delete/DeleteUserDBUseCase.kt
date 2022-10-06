package com.example.hermes.domain.usecase.delete

import com.example.hermes.domain.repository.ProfileRepository

class DeleteUserDBUseCase(
    private val profileRepository: ProfileRepository
) {

    fun execute(){
        profileRepository.deleteUserDB()
    }
}