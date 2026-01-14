package com.ruptura.app.presentation.spiritual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruptura.app.R
import com.ruptura.app.domain.model.SpiritualActivityWithStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpiritualLifeScreen(
    viewModel: SpiritualLifeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(uiState.sessionStarted) {
        if (uiState.sessionStarted) {
            viewModel.clearSessionStartedFlag()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.spiritual_life_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.activities, key = { it.activity.id }) { activityWithStatus ->
                    val schedules = uiState.schedulesByActivity[activityWithStatus.activity.id] ?: emptyList()
                    SpiritualActivityCard(
                        activityWithStatus = activityWithStatus,
                        schedules = schedules,
                        onStartSession = { viewModel.showTimeSelection(it) },
                        onMarkComplete = { viewModel.markComplete(it) },
                        onAddSchedule = { viewModel.showAddScheduleDialog(it) },
                        onEditSchedule = { viewModel.showEditScheduleDialog(it) },
                        onDeleteSchedule = { viewModel.deleteSchedule(it) },
                        onToggleSchedule = { scheduleId, enabled -> viewModel.toggleSchedule(scheduleId, enabled) },
                        isStarting = uiState.startingActivityId == activityWithStatus.activity.id
                    )
                }
            }
        }

        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.loadActivities() }) {
                            Text("Tentar novamente")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }

        if (uiState.showingTimeSelectionForActivity != null) {
            val activity = uiState.activities
                .find { it.activity.id == uiState.showingTimeSelectionForActivity }
                ?.activity

            if (activity != null) {
                TimeSelectionDialog(
                    activityName = activity.name,
                    availableDurations = activity.availableDurations,
                    selectedDuration = uiState.selectedDurationSeconds ?: activity.durationSeconds,
                    onDurationSelected = { viewModel.setSelectedDuration(it) },
                    onConfirm = { viewModel.confirmAndStartSession() },
                    onDismiss = { viewModel.cancelTimeSelection() }
                )
            }
        }

        if (uiState.showingScheduleDialogForActivity != null) {
            ScheduleDialog(
                schedule = uiState.editingSchedule,
                activityId = uiState.showingScheduleDialogForActivity!!,
                onSave = { schedule -> viewModel.saveSchedule(schedule) },
                onDismiss = { viewModel.dismissScheduleDialog() }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpiritualActivityCard(
    activityWithStatus: SpiritualActivityWithStatus,
    schedules: List<com.ruptura.app.domain.model.SpiritualSchedule>,
    onStartSession: (String) -> Unit,
    onMarkComplete: (String) -> Unit,
    onAddSchedule: (String) -> Unit,
    onEditSchedule: (com.ruptura.app.domain.model.SpiritualSchedule) -> Unit,
    onDeleteSchedule: (com.ruptura.app.domain.model.SpiritualSchedule) -> Unit,
    onToggleSchedule: (Long, Boolean) -> Unit,
    isStarting: Boolean
) {
    val activity = activityWithStatus.activity
    val isCompleted = activityWithStatus.isCompleted

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main content row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        color = if (isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = activity.getFormattedDuration(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onStartSession(activity.id) },
                        enabled = !isCompleted && !isStarting,
                        modifier = Modifier.width(100.dp)
                    ) {
                        if (isStarting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.spiritual_activity_start))
                        }
                    }

                    IconButton(
                        onClick = { onMarkComplete(activity.id) },
                        enabled = !isCompleted
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (isCompleted) "Completo" else stringResource(R.string.spiritual_activity_mark_complete),
                            tint = if (isCompleted)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Schedules section
            if (schedules.isNotEmpty() || true) { // Always show to allow adding schedules
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horários",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { onAddSchedule(activity.id) }
                        ) {
                            Text("+ Adicionar")
                        }
                    }

                    if (schedules.isEmpty()) {
                        Text(
                            text = "Nenhum horário configurado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            schedules.sortedBy { it.minutesOfDay }.forEach { schedule ->
                                ScheduleChip(
                                    schedule = schedule,
                                    onEdit = { onEditSchedule(schedule) },
                                    onDelete = { onDeleteSchedule(schedule) },
                                    onToggle = { enabled -> onToggleSchedule(schedule.id, enabled) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleChip(
    schedule: com.ruptura.app.domain.model.SpiritualSchedule,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    FilterChip(
        selected = schedule.isEnabled,
        onClick = { showMenu = true },
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(schedule.getFormattedTime())
                if (schedule.requiresExactAlarm) {
                    Text("⚡", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        leadingIcon = if (!schedule.isEnabled) {
            { Text("🔇", style = MaterialTheme.typography.labelSmall) }
        } else null
    )

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text(if (schedule.isEnabled) "Desabilitar" else "Habilitar") },
            onClick = {
                onToggle(!schedule.isEnabled)
                showMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text("Editar") },
            onClick = {
                onEdit()
                showMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text("Deletar") },
            onClick = {
                onDelete()
                showMenu = false
            }
        )
    }
}

@Composable
private fun TimeSelectionDialog(
    activityName: String,
    availableDurations: List<Int>,
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = activityName)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Selecione a duração",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableDurations.forEach { durationSeconds ->
                        val durationMinutes = durationSeconds / 60
                        FilterChip(
                            selected = selectedDuration == durationSeconds,
                            onClick = { onDurationSelected(durationSeconds) },
                            label = { Text("$durationMinutes min") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.spiritual_activity_start))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDialog(
    schedule: com.ruptura.app.domain.model.SpiritualSchedule?,
    activityId: String,
    onSave: (com.ruptura.app.domain.model.SpiritualSchedule) -> Unit,
    onDismiss: () -> Unit
) {
    val initialTime = schedule?.getLocalTime() ?: java.time.LocalTime.of(7, 0)
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    var requiresExactAlarm by remember { mutableStateOf(schedule?.requiresExactAlarm ?: false) }
    var enableSound by remember { mutableStateOf(schedule?.enableSound ?: true) }
    var enableVibration by remember { mutableStateOf(schedule?.enableVibration ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (schedule == null) "Adicionar Horário" else "Editar Horário") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time Picker
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                // Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Alarme Exato ⚡")
                    Switch(
                        checked = requiresExactAlarm,
                        onCheckedChange = { requiresExactAlarm = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Som")
                    Switch(
                        checked = enableSound,
                        onCheckedChange = { enableSound = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibração")
                    Switch(
                        checked = enableVibration,
                        onCheckedChange = { enableVibration = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutesOfDay = timePickerState.hour * 60 + timePickerState.minute
                    val periodOfDay = com.ruptura.app.domain.model.PeriodOfDay.fromMinutesOfDay(minutesOfDay)

                    val newSchedule = com.ruptura.app.domain.model.SpiritualSchedule(
                        id = schedule?.id ?: 0L,
                        activityId = activityId,
                        minutesOfDay = minutesOfDay,
                        periodOfDay = periodOfDay,
                        isEnabled = schedule?.isEnabled ?: true,
                        requiresExactAlarm = requiresExactAlarm,
                        enableSound = enableSound,
                        enableVibration = enableVibration,
                        autoRemoveAfterMinutes = null,
                        completionScope = com.ruptura.app.domain.model.CompletionScope.DAILY
                    )

                    onSave(newSchedule)
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
