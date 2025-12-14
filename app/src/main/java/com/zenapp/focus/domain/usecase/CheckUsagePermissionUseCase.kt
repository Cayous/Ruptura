package com.zenapp.focus.domain.usecase

import com.zenapp.focus.domain.repository.UsageRepository
import javax.inject.Inject

class CheckUsagePermissionUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.hasUsagePermission()
    }
}
