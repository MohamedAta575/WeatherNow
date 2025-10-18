package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    operator fun invoke(): String? {
        return repo.getCurrentUserEmail()
    }
}