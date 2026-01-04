package com.zenapp.focus.presentation.focus.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private fun formatDuration(minutes: Int): String {
    return when {
        minutes >= 60 -> "${minutes / 60}h"
        else -> "$minutes min"
    }
}

private fun formatDurationDisplay(minutes: Int): String {
    return when {
        minutes >= 60 -> {
            val hours = minutes / 60
            "$hours hora${if (hours > 1) "s" else ""}"
        }
        else -> "$minutes minuto${if (minutes > 1) "s" else ""}"
    }
}

@Composable
private fun DurationChip(
    durationMinutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(formatDuration(durationMinutes)) },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DurationSelectionGrid(
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val durations = listOf(1, 5, 10, 15, 20, 25, 30, 45, 60, 120)

    Column(modifier = modifier) {
        Text(
            text = "Selecione o tempo de foco",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            durations.forEach { duration ->
                DurationChip(
                    durationMinutes = duration,
                    isSelected = selectedDuration == duration,
                    onClick = { onDurationSelected(duration) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSetupScreen(
    viewModel: FocusSetupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSessionStarted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sessionStarted) {
        if (uiState.sessionStarted) {
            onSessionStarted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Foco") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Configure sua sessão de foco",
                style = MaterialTheme.typography.headlineSmall
            )

            DurationSelectionGrid(
                selectedDuration = uiState.focusDurationMinutes,
                onDurationSelected = { viewModel.setFocusDuration(it) }
            )

            Text(
                text = "Tempo de foco: ${formatDurationDisplay(uiState.focusDurationMinutes)}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            // Start button
            Button(
                onClick = { viewModel.startSession() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isStarting
            ) {
                if (uiState.isStarting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Iniciar Foco")
                }
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
