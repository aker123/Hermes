package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.ProfileRepository

class GetUserByLoginAndPasswordUseCase(
    private val profileRepository: ProfileRepository
) {
    fun execute(login: String, password: String): User? {
        return profileRepository.getUser(login, password)
    }
}