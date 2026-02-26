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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import com.ruptura.app.domain.model.PeriodOfDay
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
            val sortedActivities = remember(uiState.activities, uiState.schedulesByActivity) {
                uiState.activities.sortedWith(
                    compareBy(
                        { uiState.schedulesByActivity[it.activity.id]?.minOfOrNull { s -> s.minutesOfDay } ?: Int.MAX_VALUE },
                        { it.activity.orderIndex }
                    )
                )
            }

            val grouped = remember(sortedActivities, uiState.schedulesByActivity) {
                sortedActivities.groupBy { activityWithStatus ->
                    val schedules = uiState.schedulesByActivity[activityWithStatus.activity.id]
                    if (schedules.isNullOrEmpty()) null
                    else schedules.minByOrNull { it.minutesOfDay }?.periodOfDay
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                val periods = listOf(PeriodOfDay.MORNING, PeriodOfDay.AFTERNOON, PeriodOfDay.NIGHT, null)

                periods.forEach { period ->
                    val activities = grouped[period]
                    if (activities.isNullOrEmpty()) return@forEach

                    item(key = "header_${period?.name ?: "none"}") {
                        SectionHeader(period)
                    }
                    item(key = "card_${period?.name ?: "none"}") {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                activities.forEachIndexed { index, activityWithStatus ->
                                    val schedules = uiState.schedulesByActivity[activityWithStatus.activity.id] ?: emptyList()
                                    SpiritualActivityItem(
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
                                    if (index < activities.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
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

@Composable
private fun SectionHeader(period: PeriodOfDay?) {
    Text(
        text = if (period != null) "${period.getEmoji()} ${period.getDisplayName()}"
               else "Sem horário",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpiritualActivityItem(
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
    val sortedSchedules = schedules.sortedBy { it.minutesOfDay }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { onMarkComplete(activity.id) },
            enabled = !isCompleted,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = if (isCompleted) "Completo" else stringResource(R.string.spiritual_activity_mark_complete),
                tint = if (isCompleted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = activity.getFormattedDuration(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                if (sortedSchedules.isNotEmpty()) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    sortedSchedules.forEach { schedule ->
                        ScheduleChip(
                            schedule = schedule,
                            onEdit = { onEditSchedule(schedule) },
                            onDelete = { onDeleteSchedule(schedule) },
                            onToggle = { enabled -> onToggleSchedule(schedule.id, enabled) }
                        )
                    }
                    IconButton(
                        onClick = { onAddSchedule(activity.id) },
                        modifier = Modifier.size(28.dp).align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar horário",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    TextButton(
                        onClick = { onAddSchedule(activity.id) },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "+ horário",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (isStarting) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            IconButton(
                onClick = { onStartSession(activity.id) },
                enabled = !isCompleted,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.spiritual_activity_start),
                    tint = if (isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
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
