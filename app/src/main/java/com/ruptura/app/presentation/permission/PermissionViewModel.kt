package com.ruptura.app.presentation.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruptura.app.PermissionType
import com.ruptura.app.domain.usecase.CheckUsagePermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val checkUsagePermissionUseCase: CheckUsagePermissionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    private var currentPermissionType: PermissionType = PermissionType.USAGE_STATS

    fun setPermissionType(type: PermissionType) {
        currentPermissionType = type
        checkPermission()
    }

    fun checkPermission() {
        viewModelScope.launch {
            _isPermissionGranted.value = when (currentPermissionType) {
                PermissionType.USAGE_STATS -> checkUsagePermissionUseCase()
                PermissionType.OVERLAY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(context)
                    } else {
                        true
                    }
                }
            }
        }
    }

    fun openSettings(context: Context) {
        when (currentPermissionType) {
            PermissionType.USAGE_STATS -> {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
            PermissionType.OVERLAY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            }
        }
    }
}
