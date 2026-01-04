package com.zenapp.focus.data.repository

import com.zenapp.focus.data.local.dao.SpiritualActivityDao
import com.zenapp.focus.data.local.dao.SpiritualCompletionDao
import com.zenapp.focus.data.local.entity.SpiritualActivityEntity
import com.zenapp.focus.data.mapper.SpiritualMapper
import com.zenapp.focus.domain.model.CompletionType
import com.zenapp.focus.domain.model.SpiritualActivity
import com.zenapp.focus.domain.model.SpiritualActivityWithStatus
import com.zenapp.focus.domain.model.SpiritualCompletion
import com.zenapp.focus.domain.repository.SpiritualRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpiritualRepositoryImpl @Inject constructor(
    private val activityDao: SpiritualActivityDao,
    private val completionDao: SpiritualCompletionDao,
    private val mapper: SpiritualMapper
) : SpiritualRepository {

    private var activitiesCache: List<SpiritualActivity>? = null

    override suspend fun getAllActivities(): List<SpiritualActivity> =
        withContext(Dispatchers.IO) {
            activitiesCache?.let { return@withContext it }

            var activities = activityDao.getAllActivities().map { mapper.toDomain(it) }

            if (activities.isEmpty()) {
                initializeActivities()
                activities = activityDao.getAllActivities().map { mapper.toDomain(it) }
            }

            activitiesCache = activities
            activities
        }

    override suspend fun initializeActivities() =
        withContext(Dispatchers.IO) {
            val predefinedActivities = listOf(
                SpiritualActivityEntity("oferecimento_obras", "Oferecimento de obras", 30, 1),
                SpiritualActivityEntity("oracao_manha_15", "Oração da manhã (15 min)", 900, 2),
                SpiritualActivityEntity("oracao_manha_30", "Oração da manhã (30 min)", 1800, 3),
                SpiritualActivityEntity("santa_missa", "Santa missa", 1800, 4),
                SpiritualActivityEntity("visita_santissimo", "Visita ao santíssimo", 300, 5),
                SpiritualActivityEntity("leitura_novo_testamento", "Leitura do novo testamento", 300, 6),
                SpiritualActivityEntity("leitura_espiritual", "Leitura espiritual", 600, 7),
                SpiritualActivityEntity("preces", "Preces", 180, 8),
                SpiritualActivityEntity("angelus_regina_coeli", "Angelus e Regina Coeli", 60, 9),
                SpiritualActivityEntity("santo_rosario", "Santo rosário", 1200, 10),
                SpiritualActivityEntity("contemplar_rosario", "Contemplar santo rosário", 900, 11),
                SpiritualActivityEntity("oracao_tarde_15", "Oração da tarde (15 min)", 900, 12),
                SpiritualActivityEntity("oracao_tarde_30", "Oração da tarde (30 min)", 1800, 13),
                SpiritualActivityEntity("lembrai_vos", "Lembrai-vos", 30, 14),
                SpiritualActivityEntity("tres_ave_marias", "Três ave-marias para pureza", 30, 15),
                SpiritualActivityEntity("exame_consciencia", "Exame de consciência", 180, 16)
            )

            activityDao.insertActivities(predefinedActivities)
            activitiesCache = null
        }

    override suspend fun getActivitiesWithStatusForDate(date: String): List<SpiritualActivityWithStatus> =
        withContext(Dispatchers.IO) {
            val activities = getAllActivities()
            val completions = completionDao.getCompletionsForDate(date)
                .map { mapper.completionToDomain(it) }
                .associateBy { it.activityId }

            activities.map { activity ->
                SpiritualActivityWithStatus(
                    activity = activity,
                    completion = completions[activity.id]
                )
            }
        }

    override suspend fun getTodayActivitiesWithStatus(): List<SpiritualActivityWithStatus> =
        getActivitiesWithStatusForDate(LocalDate.now().toString())

    override suspend fun markActivityComplete(
        activityId: String,
        date: String,
        completionType: CompletionType,
        sessionId: Long?
    ) = withContext(Dispatchers.IO) {
        val completion = SpiritualCompletion(
            activityId = activityId,
            completionDate = date,
            completedAt = System.currentTimeMillis(),
            completionType = completionType,
            sessionId = sessionId
        )

        completionDao.insertCompletion(mapper.completionToEntity(completion))
    }

    override suspend fun isActivityCompleted(activityId: String, date: String): Boolean =
        withContext(Dispatchers.IO) {
            completionDao.getCompletion(activityId, date) != null
        }

    override suspend fun cleanupOldCompletions(daysToKeep: Int) =
        withContext(Dispatchers.IO) {
            val cutoffDate = LocalDate.now().minusDays(daysToKeep.toLong()).toString()
            completionDao.deleteOldCompletions(cutoffDate)
        }
}
