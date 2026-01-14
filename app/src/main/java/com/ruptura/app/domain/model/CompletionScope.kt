package com.ruptura.app.domain.model

enum class CompletionScope {
    DAILY, // Se completou hoje, não notifica mais nenhum horário desta atividade
    PER_SCHEDULE; // Cada horário é independente (feature futura)

    fun getDisplayName(): String {
        return when (this) {
            DAILY -> "Diário"
            PER_SCHEDULE -> "Por Horário"
        }
    }
}
