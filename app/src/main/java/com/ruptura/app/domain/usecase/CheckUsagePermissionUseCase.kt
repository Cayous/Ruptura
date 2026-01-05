package com.ruptura.app.domain.usecase

import com.ruptura.app.domain.repository.UsageRepository
import javax.inject.Inject

class CheckUsagePermissionUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.hasUsagePermission()
    }
}
