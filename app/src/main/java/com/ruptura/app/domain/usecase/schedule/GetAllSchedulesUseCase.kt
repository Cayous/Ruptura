package com.ruptura.app.domain.usecase.schedule

import com.ruptura.app.domain.model.SpiritualScheduleWithActivity
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSchedulesUseCase @Inject constructor(
    private val repository: SpiritualScheduleRepository
) {
    operator fun invoke(): Flow<List<SpiritualScheduleWithActivity>> {
        return repository.getAllSchedulesWithActivity()
    }
}
