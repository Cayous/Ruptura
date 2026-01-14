package com.ruptura.app.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create spiritual_schedules table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS spiritual_schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                activityId TEXT NOT NULL,
                minutesOfDay INTEGER NOT NULL,
                periodOfDay TEXT NOT NULL,
                isEnabled INTEGER NOT NULL DEFAULT 1,
                requiresExactAlarm INTEGER NOT NULL DEFAULT 0,
                enableSound INTEGER NOT NULL DEFAULT 1,
                enableVibration INTEGER NOT NULL DEFAULT 1,
                autoRemoveAfterMinutes INTEGER,
                completionScope TEXT NOT NULL DEFAULT 'DAILY',
                FOREIGN KEY(activityId) REFERENCES spiritual_activities(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Create index on activityId
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_spiritual_schedules_activityId
            ON spiritual_schedules(activityId)
        """.trimIndent())

        // Create schedule_snoozes table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS schedule_snoozes (
                scheduleId INTEGER PRIMARY KEY NOT NULL,
                snoozeUntilTimestamp INTEGER NOT NULL,
                activityId TEXT NOT NULL,
                FOREIGN KEY(scheduleId) REFERENCES spiritual_schedules(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Create indexes
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_schedule_snoozes_scheduleId
            ON schedule_snoozes(scheduleId)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_schedule_snoozes_activityId
            ON schedule_snoozes(activityId)
        """.trimIndent())
    }
}
