# ZenApp - Controle de Foco para Android

App Android para monitorar uso de aplicativos e ajudar a controlar o vício em celular.

## Funcionalidades

- ✅ Monitoramento de uso de apps usando APIs nativas do Android
- ✅ Top 10 apps mais usados
- ✅ Gráfico de horários de pico de uso (24h)
- ✅ Seleção de período: Hoje, 7 dias, 30 dias
- ✅ Material Design 3 com suporte a tema escuro
- ✅ Pull-to-refresh para atualizar dados

## Tecnologias

- **Kotlin** - Linguagem principal
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Design system do Google
- **Clean Architecture + MVVM** - Arquitetura escalável e testável
- **Hilt** - Injeção de dependências
- **Vico Charts** - Gráficos nativos para Compose
- **UsageStatsManager** - API nativa do Android para estatísticas de uso
- **Coroutines & Flow** - Programação assíncrona

## Requisitos

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK** 17 ou superior
- **Dispositivo Android** com Android 12 (API 31) ou superior
- **Gradle** 8.2+
- **Kotlin** 1.9.20+

⚠️ **Importante**: Este app NÃO funciona em emulador. É necessário um dispositivo físico real para testar, pois o UsageStatsManager não fornece dados em emuladores.

## Instalação e Compilação

### 1. Clone ou navegue até o diretório do projeto

```bash
cd /home/ricardo/Documentos/Programacao/zenapp
```

### 2. Abra o projeto no Android Studio

```bash
# No terminal
studio .

# Ou abra Android Studio e selecione "Open" > navegue até a pasta zenapp
```

### 3. Sync do Gradle

O Android Studio automaticamente sincronizará as dependências. Se não acontecer:
- Clique em "Sync Project with Gradle Files" (ícone de elefante com seta)
- Ou: File > Sync Project with Gradle Files

### 4. Compile o projeto

No Android Studio:
- Build > Make Project
- Ou pressione `Ctrl+F9` (Linux/Windows) / `Cmd+F9` (Mac)

### 5. Conecte um dispositivo Android físico

- Ative as "Opções do desenvolvedor" no seu Android
- Ative a "Depuração USB"
- Conecte o dispositivo via USB
- Aceite a autorização de depuração USB

### 6. Execute o app

- Clique no botão "Run" (▶️) no Android Studio
- Ou pressione `Shift+F10`
- Selecione seu dispositivo físico na lista

## Usando o App

### Primeira Execução

1. **Tela de Permissão**
   - Na primeira execução, o app solicitará a permissão `PACKAGE_USAGE_STATS`
   - Clique em "Conceder Permissão"
   - Você será redirecionado para as configurações do Android
   - Navegue até: **Configurações > Apps > Acesso especial > Acesso aos dados de uso**
   - Encontre "ZenApp" na lista e ative a permissão
   - Volte ao app (ele detectará automaticamente a permissão)

2. **Dashboard Principal**
   - Após conceder a permissão, você verá o dashboard
   - **Seletor de Período**: Escolha entre Hoje, 7 Dias ou 30 Dias
   - **Top 10 Apps**: Lista dos apps mais usados com tempo total
   - **Horários de Pico**: Gráfico de barras mostrando uso por hora (0-24h)

### Recursos

- **Pull-to-Refresh**: Arraste para baixo na tela principal para atualizar os dados
- **Ranking Visual**: Os 3 primeiros apps têm cores especiais (🥇 Ouro, 🥈 Prata, 🥉 Bronze)
- **Tema Escuro**: O app respeita a configuração de tema do sistema

## Estrutura do Projeto

```
zenapp/
├── app/src/main/java/com/zenapp/focus/
│   ├── presentation/          # UI Layer (Compose)
│   │   ├── theme/            # Cores, tipografia, tema
│   │   ├── permission/       # Tela de permissão
│   │   └── home/             # Dashboard principal
│   │       ├── components/   # Componentes reutilizáveis
│   │       ├── HomeScreen.kt
│   │       └── HomeViewModel.kt
│   │
│   ├── domain/               # Business Logic
│   │   ├── model/           # Modelos de domínio
│   │   ├── repository/      # Interface do repositório
│   │   └── usecase/         # Casos de uso
│   │
│   ├── data/                # Data Layer
│   │   ├── source/          # UsageStatsDataSource
│   │   ├── mapper/          # Conversores de dados
│   │   └── repository/      # Implementação do repositório
│   │
│   ├── di/                  # Dependency Injection (Hilt)
│   ├── MainActivity.kt
│   └── ZenApplication.kt
│
├── app/build.gradle.kts     # Dependências do app
├── build.gradle.kts         # Configuração do projeto
└── settings.gradle.kts
```

## Arquitetura

O app segue **Clean Architecture** com **MVVM**:

```
[UI] HomeScreen → [ViewModel] → [UseCase] → [Repository] → [DataSource] → UsageStatsManager
```

### Camadas

1. **Presentation**: Jetpack Compose + ViewModels
2. **Domain**: Models, UseCases, Repository interfaces (regras de negócio)
3. **Data**: Implementações de repositório, data sources, mappers

### Fluxo de Dados

1. **UI** chama método no **ViewModel**
2. **ViewModel** invoca **UseCase**
3. **UseCase** chama **Repository** (interface)
4. **RepositoryImpl** usa **DataSource** para buscar dados do **UsageStatsManager**
5. **Mapper** converte dados do Android para modelos de domínio
6. Dados retornam através das camadas como **Flow/StateFlow**
7. **UI** observa mudanças e recompõe

## Compilação via Linha de Comando

Se preferir compilar sem Android Studio:

```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

O APK será gerado em:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Instalar via ADB

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Testes

### Executar Testes Unitários

```bash
./gradlew test
```

### Executar Testes de Instrumentação

```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### "Nenhum dado disponível"

**Problema**: O app não mostra dados de uso.

**Soluções**:
1. Verifique se a permissão `PACKAGE_USAGE_STATS` está realmente concedida
2. Use o celular normalmente por alguns minutos e depois abra o app
3. Tente mudar o período para "7 Dias" ou "30 Dias"
4. Faça pull-to-refresh na tela principal

### App não compila

**Problema**: Erros de build no Gradle.

**Soluções**:
1. File > Invalidate Caches > Invalidate and Restart
2. Limpar build: `./gradlew clean`
3. Verificar conexão com internet (Gradle precisa baixar dependências)
4. Atualizar Android Studio para última versão

### Permissão não sendo detectada

**Problema**: Mesmo após conceder permissão, app continua na tela de permissão.

**Soluções**:
1. Force stop no app e abra novamente
2. Verifique em Configurações > Apps > ZenApp > Permissões
3. Revogue e conceda a permissão novamente

## Próximas Funcionalidades (Roadmap)

- [ ] **Bloqueio de Apps** - Bloquear apps após limite de tempo
- [ ] **Metas Diárias** - Definir limites de uso para apps específicos
- [ ] **Modo Foco** - Bloquear apps distracionantes temporariamente
- [ ] **Notificações** - Alertas quando ultrapassar limites
- [ ] **Histórico** - Salvar dados localmente com Room DB
- [ ] **Relatórios Semanais** - Análise de tendências e progresso
- [ ] **Widget** - Resumo de uso na home screen
- [ ] **Gamificação** - Conquistas e streak de dias

## Dependências Principais

```kotlin
// Compose
androidx.compose:compose-bom:2024.02.00

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0

// Hilt DI
com.google.dagger:hilt-android:2.50

// Vico Charts
com.patrykandpatrick.vico:compose-m3:1.13.1

// Coil (imagens)
io.coil-kt:coil-compose:2.5.0
```

## Licença

Este projeto foi criado para fins educacionais e de controle pessoal de uso de smartphone.

## Contribuindo

Sugestões e melhorias são bem-vindas! Este é um projeto inicial focado em diagnóstico de uso.

## Autor

Desenvolvido com foco em produtividade e bem-estar digital.

---

**Status**: ✅ MVP Funcional - Pronto para testes em dispositivo real
