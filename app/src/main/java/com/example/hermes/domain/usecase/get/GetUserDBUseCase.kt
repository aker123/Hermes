package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.ProfileRepository

class GetUserDBUseCase(
    private val profileRepository: ProfileRepository
) {

    fun execute(): User?{
        return profileRepository.getUserDB()
    }
}