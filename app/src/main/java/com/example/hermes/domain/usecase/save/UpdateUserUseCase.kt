package com.example.hermes.domain.usecase.save

import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.ProfileRepository

class UpdateUserUseCase(
    private val profileRepository: ProfileRepository
) {

    fun execute(user: User) {
        return profileRepository.updateUser(user)
    }
}