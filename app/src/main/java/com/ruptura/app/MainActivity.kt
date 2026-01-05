package com.ruptura.app

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruptura.app.presentation.focus.setup.FocusSetupScreen
import com.ruptura.app.presentation.home.HomeScreen
import com.ruptura.app.presentation.menu.MenuScreen
import com.ruptura.app.presentation.permission.PermissionScreen
import com.ruptura.app.presentation.spiritual.SpiritualLifeScreen
import com.ruptura.app.presentation.theme.RupturaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RupturaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ZenAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ZenAppNavigation() {
    var hasUsagePermission by remember { mutableStateOf(false) }
    var hasOverlayPermission by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Check permissions on lifecycle resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsagePermission = checkUsageStatsPermission(context)
                hasOverlayPermission = checkOverlayPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        !hasUsagePermission -> PermissionScreen(
            permissionType = PermissionType.USAGE_STATS,
            onPermissionGranted = { hasUsagePermission = true }
        )
        !hasOverlayPermission -> PermissionScreen(
            permissionType = PermissionType.OVERLAY,
            onPermissionGranted = { hasOverlayPermission = true }
        )
        else -> MainNavigationHost()
    }
}

@Composable
fun MainNavigationHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(
                onNavigateToUsageDiagnostics = { navController.navigate("home") },
                onNavigateToFocusSession = { navController.navigate("focus_setup") },
                onNavigateToSpiritualLife = { navController.navigate("spiritual_life") }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("focus_setup") {
            FocusSetupScreen(
                onNavigateBack = { navController.popBackStack() },
                onSessionStarted = {
                    // Service will handle overlay, go back to home
                    navController.popBackStack()
                }
            )
        }

        composable("spiritual_life") {
            SpiritualLifeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

enum class PermissionType {
    USAGE_STATS,
    OVERLAY
}

private fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun checkOverlayPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }
}
