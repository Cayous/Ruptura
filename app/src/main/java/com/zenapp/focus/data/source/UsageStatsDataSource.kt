package com.zenapp.focus.data.source

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process
import com.zenapp.focus.data.cache.CacheTTL
import com.zenapp.focus.data.cache.LauncherCacheEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class UsageStatsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsManager: UsageStatsManager,
    private val packageManager: PackageManager
) {
    private val launcherCache = ConcurrentHashMap<String, LauncherCacheEntry>()
    suspend fun hasUsagePermission(): Boolean = withContext(Dispatchers.IO) {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        mode == AppOpsManager.MODE_ALLOWED
    }

    suspend fun queryUsageStats(startTime: Long, endTime: Long): Map<String, UsageStats> =
        withContext(Dispatchers.IO) {
            val stats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
            stats.filterNot { isSystemApp(it.key) }
        }

    suspend fun queryUsageEvents(startTime: Long, endTime: Long): List<UsageEvent> =
        withContext(Dispatchers.IO) {
            val events = mutableListOf<UsageEvent>()
            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                if (!isSystemApp(event.packageName)) {
                    events.add(
                        UsageEvent(
                            packageName = event.packageName,
                            timestamp = event.timeStamp,
                            eventType = event.eventType
                        )
                    )
                }
            }

            events
        }

    fun getTimeRangeMillis(dayCount: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.apply {
            add(Calendar.DAY_OF_YEAR, -dayCount)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }

    fun getTodayMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }

    /**
     * Checks if the app has a launcher icon (is launchable by the user).
     * Apps without launcher icons are system services or background components.
     * Results are cached for 24 hours as launcher visibility rarely changes.
     */
    fun isLaunchable(packageName: String): Boolean {
        // Check cache
        val cached = launcherCache[packageName]
        if (cached != null && isValidLauncher(cached)) {
            return cached.isLaunchable
        }

        // Cache miss - compute
        val result = computeIsLaunchable(packageName)
        launcherCache[packageName] = LauncherCacheEntry(result)
        return result
    }

    private fun isValidLauncher(entry: LauncherCacheEntry): Boolean {
        val age = System.currentTimeMillis() - entry.timestamp
        return age < CacheTTL.LAUNCHER_MILLIS
    }

    private fun computeIsLaunchable(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
            }
            // Use PackageManager.MATCH_ALL for Android 11+ visibility
            val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PackageManager.MATCH_ALL
            } else {
                0
            }
            packageManager.queryIntentActivities(intent, flags).isNotEmpty()
        } catch (e: Exception) {
            false  // Conservative: if can't determine, assume not launchable
        }
    }

    /**
     * Determines if an app should be filtered from usage statistics.
     *
     * Decision hierarchy:
     * 1. Explicit never-show list (MIUI/system components)
     * 2. Self-package (ZenApp itself)
     * 3. System flags (FLAG_SYSTEM + FLAG_UPDATED_SYSTEM_APP)
     * 4. System package prefixes (com.android.*, com.miui.*, com.xiaomi.*)
     * 5. Launcher visibility (no launcher icon = system service)
     */
    private fun isSystemApp(packageName: String): Boolean {
        // Step 1: Explicit MIUI/system components that leak through
        val neverShow = setOf(
            "com.miui.home",                              // MIUI Launcher
            "com.miui.securitycenter",                    // MIUI Security
            "com.xiaomi.discover",                        // MIUI App Vault
            "com.android.systemui",                       // Android System UI
            "com.google.android.gms",                     // Google Play Services
            "com.google.android.gsf",                     // Google Services Framework
            "com.google.android.inputmethod.latin",       // Google Keyboard
            "com.android.vending"                         // Play Store
        )

        if (packageName in neverShow) return true

        // Step 2: Filter self (ZenApp)
        if (packageName == context.packageName) return true

        // Step 3: Get ApplicationInfo for flag checks
        val appInfo = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: Exception) {
            return true  // Can't access = likely system component
        }

        // Step 4: Check system flags
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        if (isSystemApp) {
            // ALLOW updated system apps (Chrome, YouTube, Gmail, Maps)
            val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            if (isUpdatedSystemApp) {
                return false  // User has updated it = real app
            }

            // Step 5: Filter system package prefixes (NOTE: NO com.google.android.*)
            val systemPrefixes = listOf(
                "com.android.",
                "com.miui.",
                "com.xiaomi."
            )

            if (systemPrefixes.any { packageName.startsWith(it) }) {
                return true
            }
        }

        // Step 6: Filter non-launchable apps (system services)
        if (!isLaunchable(packageName)) {
            return true
        }

        return false  // Passed all filters = legitimate user app
    }

    data class UsageEvent(
        val packageName: String,
        val timestamp: Long,
        val eventType: Int
    )
}
