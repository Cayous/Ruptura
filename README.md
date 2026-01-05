# Ruptura - Combate Interior

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-12%2B-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20%2B-purple.svg)](https://kotlinlang.org)
[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-orange.svg)](https://m3.material.io/)

> Um cinto de segurança digital para sua atenção e bem-estar
> A digital safety belt for your attention and well-being

**[🇧🇷 Português](#português)** | **[🇺🇸 English](#english)**

---

<a name="português"></a>
# 🇧🇷 Português

## Índice

- [Por Que Este App Existe?](#por-que-este-app-existe)
- [Sobre](#sobre)
- [Funcionalidades](#funcionalidades)
- [Screenshots](#screenshots)
- [Instalação](#instalação)
- [Como Usar](#como-usar)
- [Tecnologias](#tecnologias)
- [Requisitos](#requisitos)
- [Arquitetura](#arquitetura)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Compilação](#compilação)
- [Testes](#testes)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [Contribuindo](#contribuindo)
- [Licença](#licença)
- [Autor](#autor)

---

## Por Que Este App Existe?

Quando os carros foram criados, o índice de acidentes era gigantesco. Com o tempo, foram sendo desenvolvidas ferramentas de segurança - semáforos, cintos de segurança, airbags - para reduzir os riscos e agressões ao ser humano advindos dessa tecnologia.

**Este app é um desses recursos de segurança.**

Somos a primeira geração a usar smartphones. Ainda não temos as ferramentas necessárias para administrar essa nova tecnologia de forma responsável. O vício em celular não é uma falha de caráter - é a falta de mecanismos de proteção adequados.

**Ruptura** é um cinto de segurança digital para sua atenção e bem-estar. Foi criado para oferecer **controle e segurança** contra o vício em smartphone, ajudando você a recuperar seu foco, produtividade e, especialmente, sua vida espiritual.

---

## Sobre

**Ruptura** é um aplicativo Android open-source focado em combate ao vício digital e recuperação da atenção. Combina três pilares fundamentais:

1. **Diagnóstico de Uso**: Monitore em tempo real quais apps estão consumindo sua atenção
2. **Sessões de Foco**: Bloqueie distrações com sessões cronometradas e tela de bloqueio
3. **Vida Espiritual**: Integre práticas espirituais diárias ao seu combate interior

Diferente de outros apps de produtividade, **Ruptura** reconhece que o combate ao vício digital é também um combate espiritual. Por isso, oferece funcionalidades específicas para ajudar você a manter disciplina não apenas profissional, mas também espiritual.

---

## Funcionalidades

### 📊 Diagnóstico de Uso de Apps

- **Monitoramento em tempo real** usando APIs nativas do Android (`UsageStatsManager`)
- **Top 10 apps mais usados** com ranking visual (Ouro, Prata, Bronze)
- **Gráfico de horários de pico** mostrando padrões de uso por hora (0-24h)
- **Seleção de período**: Hoje, 7 dias ou 30 dias
- **Pull-to-refresh** para atualizar dados instantaneamente
- **Material Design 3** com suporte a tema escuro

### 🔒 Sessões de Foco

- **Sessões customizáveis** de 1 a 120 minutos
- **Botões predefinidos** para seleção rápida (1, 5, 10, 15, 20, 25, 30, 45, 60, 120 minutos)
- **Tela de bloqueio overlay** que impede troca de apps durante foco
- **Múltiplos ciclos** com períodos de descanso entre sessões
- **Estatísticas de sessão**: tempo total de foco, número de pausas, etc.
- **Foreground service** para operação contínua mesmo com app em segundo plano
- **Estados da sessão**: SETUP, FOCUS_ACTIVE, BREAK_ACTIVE, PAUSED, COMPLETED, CANCELLED

### 📿 Atividades Espirituais

- **Atividades predefinidas** de práticas espirituais com duração configurável
- **Rastreamento de status**: completado/pendente para o dia
- **Integração com sessões de foco**: vincule sessões a atividades espirituais
- **Persistência local**: histórico de completude armazenado no banco de dados
- **Indicadores visuais** de progresso diário

---

## Screenshots

### Menu Principal
![Menu Principal](screenshots/menu.png)
*Tela inicial com acesso às três funcionalidades principais*

### Dashboard de Uso
![Dashboard de Uso](screenshots/dashboard.png)
*Top 10 apps mais usados e gráfico de horários de pico*

### Configuração de Sessão de Foco
![Configuração de Foco](screenshots/focus-setup.png)
*Interface para configurar duração e ciclos da sessão de foco*

### Tela de Bloqueio Ativa
![Tela de Bloqueio](screenshots/lock-screen.png)
*Overlay de bloqueio durante sessão de foco ativa*

### Atividades Espirituais
![Atividades Espirituais](screenshots/spiritual-activities.png)
*Lista de práticas espirituais diárias com status de completude*

---

## Instalação

### Opção 1: Compilar do Código-Fonte (Recomendado)

#### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/ruptura.git
cd ruptura
```

#### 2. Abra no Android Studio
```bash
# No terminal
studio .

# Ou abra Android Studio e selecione "Open" > navegue até a pasta ruptura
```

#### 3. Sincronize o Gradle
O Android Studio automaticamente sincronizará as dependências. Se não acontecer:
- Clique em "Sync Project with Gradle Files"
- Ou: File > Sync Project with Gradle Files

#### 4. Conecte um dispositivo Android físico
- Ative as "Opções do desenvolvedor" no seu Android
- Ative a "Depuração USB"
- Conecte o dispositivo via USB
- Aceite a autorização de depuração USB

⚠️ **Importante**: Este app NÃO funciona em emulador. É necessário um dispositivo físico real, pois o `UsageStatsManager` não fornece dados em emuladores.

#### 5. Execute o app
- Clique no botão "Run" (▶️) no Android Studio
- Ou pressione `Shift+F10`

### Opção 2: APK Pré-compilado

Baixe o APK mais recente da página de [Releases](https://github.com/seu-usuario/ruptura/releases):

```bash
# Instalar via ADB
adb install ruptura-v1.0.0.apk
```

Ou transfira o APK para seu dispositivo e instale manualmente.

### Opção 3: Lojas de Aplicativos (Em Breve)

- **Google Play Store**: Em breve
- **F-Droid**: Em breve

---

## Como Usar

### Primeira Execução - Conceder Permissões

#### Passo 1: Permissão de Estatísticas de Uso
1. Na primeira execução, o app solicitará `PACKAGE_USAGE_STATS`
2. Toque em "Conceder Permissão"
3. Você será redirecionado para Configurações do Android
4. Navegue: **Configurações > Apps > Acesso especial > Acesso aos dados de uso**
5. Encontre "Ruptura" e ative a permissão
6. Volte ao app (permissão detectada automaticamente)

#### Passo 2: Permissão de Sobreposição (Para Sessões de Foco)
1. Ao iniciar uma sessão de foco, será solicitada `SYSTEM_ALERT_WINDOW`
2. Toque em "Conceder Permissão"
3. Navegue: **Configurações > Apps > Acesso especial > Exibir sobre outros apps**
4. Encontre "Ruptura" e ative a permissão

### Menu Principal

No menu principal, você tem acesso a três funcionalidades:

1. **Diagnóstico de Uso**: Visualize seus hábitos de uso
2. **Sessão de Foco**: Inicie uma sessão de trabalho focado
3. **Vida Espiritual**: Gerencie suas atividades espirituais diárias

### Usando o Diagnóstico de Uso

1. Toque em "Diagnóstico de Uso" no menu
2. Selecione o período desejado: Hoje, 7 Dias ou 30 Dias
3. Veja o **Top 10 apps mais usados** com tempo total
4. Analise o **Gráfico de Horários de Pico** para identificar padrões
5. Arraste para baixo para atualizar os dados (pull-to-refresh)

### Iniciando uma Sessão de Foco

1. Toque em "Sessão de Foco" no menu
2. Selecione a duração:
   - Use os botões rápidos (1, 5, 10, 15, 20, 25, 30, 45, 60, 120 min)
   - Ou digite um valor personalizado (1-120 minutos)
3. Configure o número de ciclos (opcional)
4. Toque em "Iniciar Sessão"
5. A tela de bloqueio será ativada, impedindo distrações
6. Complete a sessão ou toque em "Cancelar" se necessário

### Gerenciando Atividades Espirituais

1. Toque em "Vida Espiritual" no menu
2. Veja a lista de atividades espirituais do dia
3. Toque em uma atividade para iniciar uma sessão dedicada
4. Complete a atividade e ela será marcada como concluída
5. Acompanhe seu progresso espiritual diário

---

## Tecnologias

### Core Android & Arquitetura
- **Kotlin** 1.9.20+ - Linguagem principal
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Design system do Google
- **Clean Architecture + MVVM** - Arquitetura escalável e testável
- **Hilt** - Injeção de dependências
- **Coroutines & Flow** - Programação reativa e assíncrona
- **Room Database** - Persistência local
- **Jetpack Navigation Compose** - Navegação declarativa

### Bibliotecas Principais
```kotlin
// Compose
androidx.compose:compose-bom:2024.02.00
androidx.compose.material3

// Navigation
androidx.navigation:navigation-compose:2.7.7

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose
androidx.lifecycle:lifecycle-service

// Hilt DI
com.google.dagger:hilt-android:2.50

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Charts
com.patrykandpatrick.vico:compose-m3:1.13.1

// Image Loading
io.coil-kt:coil-compose:2.5.0

// Accompanist
com.google.accompanist:accompanist-drawablepainter

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
```

### APIs Nativas do Android
- **UsageStatsManager** - Estatísticas de uso de apps
- **PackageManager** - Informações de pacotes instalados
- **Foreground Service** - Serviço persistente para sessões de foco
- **SYSTEM_ALERT_WINDOW** - Overlay de bloqueio de tela

---

## Requisitos

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK** 17 ou superior
- **Dispositivo Android** com Android 12 (API 31) ou superior
- **Gradle** 8.2+
- **Kotlin** 1.9.20+

**Permissões necessárias**:
- `PACKAGE_USAGE_STATS` - Para monitorar uso de apps
- `SYSTEM_ALERT_WINDOW` - Para overlay de bloqueio durante foco

---

## Arquitetura

O app segue **Clean Architecture** com **MVVM**:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  (Jetpack Compose UI + ViewModels + Navigation)             │
│  - HomeScreen, FocusSetupScreen, SpiritualLifeScreen        │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                            │
│  (Business Logic - Pure Kotlin)                              │
│  - Models, UseCases, Repository Interfaces                   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  (Repository Implementations, DataSources, Room DB)          │
│  - UsageRepositoryImpl, FocusSessionRepositoryImpl          │
│  - UsageStatsDataSource, Room DAOs                          │
└─────────────────────────────────────────────────────────────┘
```

### Fluxo de Dados

1. **UI** (Compose) chama método no **ViewModel**
2. **ViewModel** invoca **UseCase** (lógica de negócio)
3. **UseCase** chama **Repository** (interface do domínio)
4. **RepositoryImpl** usa **DataSource** ou **DAO** para buscar dados
5. **Mapper** converte dados externos para modelos de domínio
6. Dados retornam através das camadas como **Flow/StateFlow**
7. **UI** observa mudanças e recompõe automaticamente

### Injeção de Dependências (Hilt)

```kotlin
@HiltAndroidApp
class RupturaApplication : Application()

// Modules
- AppModule: UsageStatsManager, PackageManager
- DatabaseModule: Room Database
- ServiceModule: FocusLockService dependencies
```

---

## Estrutura do Projeto

```
ruptura/
├── app/src/main/java/com/ruptura/app/
│   ├── presentation/              # UI Layer (Compose)
│   │   ├── theme/                # Cores, tipografia, tema
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   ├── menu/                 # Tela de menu principal
│   │   │   └── MenuScreen.kt
│   │   ├── permission/           # Tela de permissões
│   │   │   ├── PermissionScreen.kt
│   │   │   └── PermissionViewModel.kt
│   │   ├── home/                 # Dashboard de uso
│   │   │   ├── components/
│   │   │   │   ├── TopAppsList.kt
│   │   │   │   ├── PeakHoursChart.kt
│   │   │   │   └── AppUsageCard.kt
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── focus/                # Sessões de foco
│   │   │   ├── setup/
│   │   │   │   ├── FocusSetupScreen.kt
│   │   │   │   └── FocusSetupViewModel.kt
│   │   │   └── lock/
│   │   │       └── LockScreenContent.kt
│   │   └── spiritual/            # Vida espiritual
│   │       ├── SpiritualLifeScreen.kt
│   │       ├── SpiritualLifeViewModel.kt
│   │       └── SpiritualLifeUiState.kt
│   │
│   ├── domain/                   # Business Logic
│   │   ├── model/               # Modelos de domínio
│   │   │   ├── AppUsageInfo.kt
│   │   │   ├── DailyUsage.kt
│   │   │   ├── HourlyUsage.kt
│   │   │   ├── FocusSession.kt
│   │   │   ├── SessionConfig.kt
│   │   │   ├── SessionStats.kt
│   │   │   ├── SpiritualActivity.kt
│   │   │   └── SpiritualCompletion.kt
│   │   ├── repository/          # Interfaces
│   │   │   ├── UsageRepository.kt
│   │   │   ├── FocusSessionRepository.kt
│   │   │   └── SpiritualRepository.kt
│   │   └── usecase/             # Casos de uso
│   │       ├── GetTopUsedAppsUseCase.kt
│   │       ├── GetPeakHoursUseCase.kt
│   │       ├── CheckUsagePermissionUseCase.kt
│   │       ├── session/
│   │       │   ├── StartFocusSessionUseCase.kt
│   │       │   ├── CompleteSessionUseCase.kt
│   │       │   ├── BreakFocusSessionUseCase.kt
│   │       │   └── UpdateSessionPhaseUseCase.kt
│   │       └── spiritual/
│   │           ├── GetTodaySpiritualActivitiesUseCase.kt
│   │           ├── MarkSpiritualActivityCompleteUseCase.kt
│   │           └── StartSpiritualActivitySessionUseCase.kt
│   │
│   ├── data/                    # Data Layer
│   │   ├── local/              # Room Database
│   │   │   ├── FocusDatabase.kt
│   │   │   ├── entity/
│   │   │   │   ├── SessionEntity.kt
│   │   │   │   ├── SessionStatsEntity.kt
│   │   │   │   ├── SpiritualActivityEntity.kt
│   │   │   │   └── SpiritualCompletionEntity.kt
│   │   │   └── dao/
│   │   │       ├── SessionDao.kt
│   │   │       ├── SessionStatsDao.kt
│   │   │       ├── SpiritualActivityDao.kt
│   │   │       └── SpiritualCompletionDao.kt
│   │   ├── source/             # Data sources
│   │   │   └── UsageStatsDataSource.kt
│   │   ├── cache/              # Caching
│   │   │   └── UsageDataCache.kt
│   │   ├── mapper/             # Conversores
│   │   │   ├── UsageStatsMapper.kt
│   │   │   ├── SessionMapper.kt
│   │   │   └── SpiritualMapper.kt
│   │   └── repository/         # Implementações
│   │       ├── UsageRepositoryImpl.kt
│   │       ├── FocusSessionRepositoryImpl.kt
│   │       └── SpiritualRepositoryImpl.kt
│   │
│   ├── service/                # Android Services
│   │   └── FocusLockService.kt
│   │
│   ├── di/                     # Dependency Injection
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   └── ServiceModule.kt
│   │
│   ├── MainActivity.kt
│   └── RupturaApplication.kt
│
├── app/build.gradle.kts        # Dependências do app
├── build.gradle.kts            # Configuração do projeto
├── settings.gradle.kts
├── CONTRIBUTING.md             # Guia de contribuição
├── LICENSE                     # Apache 2.0
└── README.md                   # Este arquivo
```

---

## Compilação

### Via Android Studio

1. Build > Make Project
2. Ou pressione `Ctrl+F9` (Linux/Windows) / `Cmd+F9` (Mac)

### Via Linha de Comando

#### Build Debug APK
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

#### Build Release APK (Signed)
```bash
./gradlew assembleRelease
```

#### Instalar via ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Limpar Build
```bash
./gradlew clean
```

---

## Testes

### Executar Testes Unitários
```bash
./gradlew test
```

### Executar Testes de Instrumentação (Requer dispositivo)
```bash
./gradlew connectedAndroidTest
```

### Gerar Relatório de Cobertura
```bash
./gradlew jacocoTestReport
```

---

## Troubleshooting

### "Nenhum dado disponível" no Dashboard

**Problema**: O app não mostra dados de uso.

**Soluções**:
1. Verifique se a permissão `PACKAGE_USAGE_STATS` está realmente concedida
2. Use o celular normalmente por alguns minutos e depois abra o app
3. Tente mudar o período para "7 Dias" ou "30 Dias"
4. Faça pull-to-refresh na tela principal
5. Force stop no app e abra novamente

### App não compila

**Problema**: Erros de build no Gradle.

**Soluções**:
1. File > Invalidate Caches > Invalidate and Restart
2. Limpar build: `./gradlew clean`
3. Verificar conexão com internet (Gradle precisa baixar dependências)
4. Atualizar Android Studio para última versão
5. Verificar se JDK 17 está instalado

### Permissão não sendo detectada

**Problema**: Mesmo após conceder permissão, app continua na tela de permissão.

**Soluções**:
1. Force stop no app e abra novamente
2. Verifique em Configurações > Apps > Ruptura > Permissões
3. Revogue e conceda a permissão novamente
4. Reinicie o dispositivo

### Tela de bloqueio não aparece

**Problema**: Sessão de foco iniciada mas tela de bloqueio não aparece.

**Soluções**:
1. Verifique se a permissão `SYSTEM_ALERT_WINDOW` está concedida
2. Configurações > Apps > Acesso especial > Exibir sobre outros apps > Ruptura (Ativo)
3. Alguns launchers personalizados podem bloquear overlays
4. Tente usar o launcher padrão do Android

### App fecha sozinho durante sessão de foco

**Problema**: O foreground service é encerrado pelo sistema.

**Soluções**:
1. Desative otimização de bateria para Ruptura
2. Configurações > Bateria > Otimização de bateria > Ruptura > Não otimizar
3. Alguns fabricantes (Xiaomi, Huawei) têm gerenciadores de bateria agressivos
4. Adicione Ruptura à lista de apps protegidos/autoiniciados

---

## Roadmap

Funcionalidades planejadas para versões futuras:

- [ ] **Bloqueio seletivo de apps** - Bloquear apps específicos após limite de tempo
- [ ] **Metas personalizadas** - Definir limites diários para apps individuais
- [ ] **Notificações inteligentes** - Alertas quando ultrapassar limites configurados
- [ ] **Relatórios semanais/mensais** - Análise de tendências e progresso
- [ ] **Widget para home screen** - Resumo de uso e acesso rápido a sessões
- [ ] **Gamificação** - Conquistas, streaks e sistema de recompensas
- [ ] **Sincronização em nuvem** - Backup e sincronização entre dispositivos
- [ ] **Modo família** - Monitoramento parental e limites para crianças
- [ ] **Integração com calendário** - Bloqueio automático durante eventos
- [ ] **Exportação de dados** - CSV, JSON para análise externa
- [ ] **Mais atividades espirituais** - Biblioteca expansível de práticas
- [ ] **Lembretes de atividades espirituais** - Notificações customizáveis
- [ ] **Diário espiritual** - Registro de reflexões e progresso

---

## Contribuindo

Contribuições são muito bem-vindas! Este projeto é open-source e foi criado para ajudar a comunidade.

Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

Por favor, leia [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes sobre nosso código de conduta e processo de contribuição.

---

## Licença

Este projeto está licenciado sob a **Apache License 2.0** - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
Copyright 2024 Ricardo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## Autor

**Ricardo**

Desenvolvedor focado em **bem-estar digital** e **tecnologia consciente**.

### Missão Pessoal

Somos a primeira geração a usar smartphones. Não escolhemos nascer nesta era de distração digital constante, mas podemos escolher como responder a ela.

Este app nasceu da minha própria luta contra o vício digital. Percebi que, assim como os carros precisaram de cintos de segurança, nós precisamos de ferramentas para navegar a era digital com segurança.

**Ruptura** não é apenas sobre produtividade - é sobre recuperar sua atenção, seu tempo e sua vida espiritual. É sobre criar um espaço para o que realmente importa.

Espero que este app ajude você tanto quanto tem me ajudado. Se você também acredita que podemos usar a tecnologia de forma mais consciente e saudável, considere contribuir com o projeto ou compartilhá-lo com pessoas que possam se beneficiar.

**Combate Interior** é uma jornada diária. Vamos caminhar juntos.

---

**Contato**: [Seu Email ou GitHub]

**Status**: ✅ MVP Funcional - Versão 1.0

---
---
---

<a name="english"></a>
# 🇺🇸 English

## Table of Contents

- [Why This App Exists?](#why-this-app-exists)
- [About](#about)
- [Features](#features)
- [Screenshots](#screenshots-en)
- [Installation](#installation-en)
- [How to Use](#how-to-use)
- [Technologies](#technologies-en)
- [Requirements](#requirements-en)
- [Architecture](#architecture-en)
- [Project Structure](#project-structure)
- [Building](#building)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting-en)
- [Roadmap](#roadmap-en)
- [Contributing](#contributing-en)
- [License](#license-en)
- [Author](#author-en)

---

## Why This App Exists?

When cars were first invented, accident rates were enormous. Over time, safety tools were developed - traffic lights, seatbelts, airbags - to reduce the risks and harm to humans from this technology.

**This app is one of these safety resources.**

We are the first generation to use smartphones. We don't yet have the necessary tools to manage this new technology responsibly. Phone addiction is not a character flaw - it's the lack of adequate protection mechanisms.

**Ruptura** (Portuguese for "Break/Rupture") is a digital safety belt for your attention and well-being. It was created to offer **control and security** against smartphone addiction, helping you recover your focus, productivity, and especially your spiritual life.

---

## About

**Ruptura** is an open-source Android app focused on combating digital addiction and recovering attention. It combines three fundamental pillars:

1. **Usage Diagnostics**: Monitor in real-time which apps are consuming your attention
2. **Focus Sessions**: Block distractions with timed sessions and lock screen
3. **Spiritual Life**: Integrate daily spiritual practices into your interior combat

Unlike other productivity apps, **Ruptura** recognizes that combating digital addiction is also a spiritual combat. That's why it offers specific features to help you maintain discipline not only professionally, but also spiritually.

---

## Features

### 📊 App Usage Diagnostics

- **Real-time monitoring** using native Android APIs (`UsageStatsManager`)
- **Top 10 most-used apps** with visual ranking (Gold, Silver, Bronze)
- **Peak hours chart** showing usage patterns by hour (0-24h)
- **Period selection**: Today, 7 days, or 30 days
- **Pull-to-refresh** to update data instantly
- **Material Design 3** with dark theme support

### 🔒 Focus Sessions

- **Customizable sessions** from 1 to 120 minutes
- **Predefined buttons** for quick selection (1, 5, 10, 15, 20, 25, 30, 45, 60, 120 minutes)
- **Lock screen overlay** that prevents app switching during focus
- **Multiple cycles** with rest periods between sessions
- **Session statistics**: total focus time, number of breaks, etc.
- **Foreground service** for continuous operation even with app in background
- **Session states**: SETUP, FOCUS_ACTIVE, BREAK_ACTIVE, PAUSED, COMPLETED, CANCELLED

### 📿 Spiritual Activities

- **Predefined activities** of spiritual practices with configurable duration
- **Status tracking**: completed/pending for the day
- **Focus session integration**: link sessions to spiritual activities
- **Local persistence**: completion history stored in database
- **Visual indicators** of daily progress

---

<a name="screenshots-en"></a>
## Screenshots

### Main Menu
![Main Menu](screenshots/menu.png)
*Home screen with access to the three main features*

### Usage Dashboard
![Usage Dashboard](screenshots/dashboard.png)
*Top 10 most-used apps and peak hours chart*

### Focus Session Setup
![Focus Setup](screenshots/focus-setup.png)
*Interface to configure duration and cycles of focus session*

### Active Lock Screen
![Lock Screen](screenshots/lock-screen.png)
*Blocking overlay during active focus session*

### Spiritual Activities
![Spiritual Activities](screenshots/spiritual-activities.png)
*List of daily spiritual practices with completion status*

---

<a name="installation-en"></a>
## Installation

### Option 1: Build from Source (Recommended)

#### 1. Clone the repository
```bash
git clone https://github.com/your-username/ruptura.git
cd ruptura
```

#### 2. Open in Android Studio
```bash
# In terminal
studio .

# Or open Android Studio and select "Open" > navigate to ruptura folder
```

#### 3. Sync Gradle
Android Studio will automatically sync dependencies. If it doesn't:
- Click "Sync Project with Gradle Files"
- Or: File > Sync Project with Gradle Files

#### 4. Connect a physical Android device
- Enable "Developer options" on your Android
- Enable "USB debugging"
- Connect device via USB
- Accept USB debugging authorization

⚠️ **Important**: This app does NOT work on emulator. A real physical device is required, as `UsageStatsManager` doesn't provide data on emulators.

#### 5. Run the app
- Click the "Run" button (▶️) in Android Studio
- Or press `Shift+F10`

### Option 2: Pre-built APK

Download the latest APK from the [Releases](https://github.com/your-username/ruptura/releases) page:

```bash
# Install via ADB
adb install ruptura-v1.0.0.apk
```

Or transfer the APK to your device and install manually.

### Option 3: App Stores (Coming Soon)

- **Google Play Store**: Coming soon
- **F-Droid**: Coming soon

---

## How to Use

### First Run - Grant Permissions

#### Step 1: Usage Statistics Permission
1. On first run, the app will request `PACKAGE_USAGE_STATS`
2. Tap "Grant Permission"
3. You'll be redirected to Android Settings
4. Navigate: **Settings > Apps > Special access > Usage access**
5. Find "Ruptura" and enable the permission
6. Return to the app (permission detected automatically)

#### Step 2: Overlay Permission (For Focus Sessions)
1. When starting a focus session, `SYSTEM_ALERT_WINDOW` will be requested
2. Tap "Grant Permission"
3. Navigate: **Settings > Apps > Special access > Display over other apps**
4. Find "Ruptura" and enable the permission

### Main Menu

In the main menu, you have access to three features:

1. **Usage Diagnostics**: View your usage habits
2. **Focus Session**: Start a focused work session
3. **Spiritual Life**: Manage your daily spiritual activities

### Using Usage Diagnostics

1. Tap "Usage Diagnostics" in the menu
2. Select desired period: Today, 7 Days, or 30 Days
3. See **Top 10 most-used apps** with total time
4. Analyze the **Peak Hours Chart** to identify patterns
5. Swipe down to refresh data (pull-to-refresh)

### Starting a Focus Session

1. Tap "Focus Session" in the menu
2. Select duration:
   - Use quick buttons (1, 5, 10, 15, 20, 25, 30, 45, 60, 120 min)
   - Or enter a custom value (1-120 minutes)
3. Configure number of cycles (optional)
4. Tap "Start Session"
5. Lock screen will activate, preventing distractions
6. Complete the session or tap "Cancel" if needed

### Managing Spiritual Activities

1. Tap "Spiritual Life" in the menu
2. See the list of spiritual activities for the day
3. Tap an activity to start a dedicated session
4. Complete the activity and it will be marked as done
5. Track your daily spiritual progress

---

<a name="technologies-en"></a>
## Technologies

### Core Android & Architecture
- **Kotlin** 1.9.20+ - Primary language
- **Jetpack Compose** - Modern declarative UI
- **Material Design 3** - Google's design system
- **Clean Architecture + MVVM** - Scalable and testable architecture
- **Hilt** - Dependency injection
- **Coroutines & Flow** - Reactive and asynchronous programming
- **Room Database** - Local persistence
- **Jetpack Navigation Compose** - Declarative navigation

### Main Libraries
```kotlin
// Compose
androidx.compose:compose-bom:2024.02.00
androidx.compose.material3

// Navigation
androidx.navigation:navigation-compose:2.7.7

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose
androidx.lifecycle:lifecycle-service

// Hilt DI
com.google.dagger:hilt-android:2.50

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Charts
com.patrykandpatrick.vico:compose-m3:1.13.1

// Image Loading
io.coil-kt:coil-compose:2.5.0

// Accompanist
com.google.accompanist:accompanist-drawablepainter

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
```

### Native Android APIs
- **UsageStatsManager** - App usage statistics
- **PackageManager** - Installed package information
- **Foreground Service** - Persistent service for focus sessions
- **SYSTEM_ALERT_WINDOW** - Screen lock overlay

---

<a name="requirements-en"></a>
## Requirements

- **Android Studio** Hedgehog (2023.1.1) or higher
- **JDK** 17 or higher
- **Android Device** with Android 12 (API 31) or higher
- **Gradle** 8.2+
- **Kotlin** 1.9.20+

**Required permissions**:
- `PACKAGE_USAGE_STATS` - To monitor app usage
- `SYSTEM_ALERT_WINDOW` - For lock overlay during focus

---

<a name="architecture-en"></a>
## Architecture

The app follows **Clean Architecture** with **MVVM**:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  (Jetpack Compose UI + ViewModels + Navigation)             │
│  - HomeScreen, FocusSetupScreen, SpiritualLifeScreen        │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                            │
│  (Business Logic - Pure Kotlin)                              │
│  - Models, UseCases, Repository Interfaces                   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  (Repository Implementations, DataSources, Room DB)          │
│  - UsageRepositoryImpl, FocusSessionRepositoryImpl          │
│  - UsageStatsDataSource, Room DAOs                          │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **UI** (Compose) calls method on **ViewModel**
2. **ViewModel** invokes **UseCase** (business logic)
3. **UseCase** calls **Repository** (domain interface)
4. **RepositoryImpl** uses **DataSource** or **DAO** to fetch data
5. **Mapper** converts external data to domain models
6. Data returns through layers as **Flow/StateFlow**
7. **UI** observes changes and recomposes automatically

### Dependency Injection (Hilt)

```kotlin
@HiltAndroidApp
class RupturaApplication : Application()

// Modules
- AppModule: UsageStatsManager, PackageManager
- DatabaseModule: Room Database
- ServiceModule: FocusLockService dependencies
```

---

## Project Structure

```
ruptura/
├── app/src/main/java/com/ruptura/app/
│   ├── presentation/              # UI Layer (Compose)
│   │   ├── theme/                # Colors, typography, theme
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   ├── menu/                 # Main menu screen
│   │   │   └── MenuScreen.kt
│   │   ├── permission/           # Permission screens
│   │   │   ├── PermissionScreen.kt
│   │   │   └── PermissionViewModel.kt
│   │   ├── home/                 # Usage dashboard
│   │   │   ├── components/
│   │   │   │   ├── TopAppsList.kt
│   │   │   │   ├── PeakHoursChart.kt
│   │   │   │   └── AppUsageCard.kt
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── focus/                # Focus sessions
│   │   │   ├── setup/
│   │   │   │   ├── FocusSetupScreen.kt
│   │   │   │   └── FocusSetupViewModel.kt
│   │   │   └── lock/
│   │   │       └── LockScreenContent.kt
│   │   └── spiritual/            # Spiritual life
│   │       ├── SpiritualLifeScreen.kt
│   │       ├── SpiritualLifeViewModel.kt
│   │       └── SpiritualLifeUiState.kt
│   │
│   ├── domain/                   # Business Logic
│   │   ├── model/               # Domain models
│   │   │   ├── AppUsageInfo.kt
│   │   │   ├── DailyUsage.kt
│   │   │   ├── HourlyUsage.kt
│   │   │   ├── FocusSession.kt
│   │   │   ├── SessionConfig.kt
│   │   │   ├── SessionStats.kt
│   │   │   ├── SpiritualActivity.kt
│   │   │   └── SpiritualCompletion.kt
│   │   ├── repository/          # Interfaces
│   │   │   ├── UsageRepository.kt
│   │   │   ├── FocusSessionRepository.kt
│   │   │   └── SpiritualRepository.kt
│   │   └── usecase/             # Use cases
│   │       ├── GetTopUsedAppsUseCase.kt
│   │       ├── GetPeakHoursUseCase.kt
│   │       ├── CheckUsagePermissionUseCase.kt
│   │       ├── session/
│   │       │   ├── StartFocusSessionUseCase.kt
│   │       │   ├── CompleteSessionUseCase.kt
│   │       │   ├── BreakFocusSessionUseCase.kt
│   │       │   └── UpdateSessionPhaseUseCase.kt
│   │       └── spiritual/
│   │           ├── GetTodaySpiritualActivitiesUseCase.kt
│   │           ├── MarkSpiritualActivityCompleteUseCase.kt
│   │           └── StartSpiritualActivitySessionUseCase.kt
│   │
│   ├── data/                    # Data Layer
│   │   ├── local/              # Room Database
│   │   │   ├── FocusDatabase.kt
│   │   │   ├── entity/
│   │   │   │   ├── SessionEntity.kt
│   │   │   │   ├── SessionStatsEntity.kt
│   │   │   │   ├── SpiritualActivityEntity.kt
│   │   │   │   └── SpiritualCompletionEntity.kt
│   │   │   └── dao/
│   │   │       ├── SessionDao.kt
│   │   │       ├── SessionStatsDao.kt
│   │   │       ├── SpiritualActivityDao.kt
│   │   │       └── SpiritualCompletionDao.kt
│   │   ├── source/             # Data sources
│   │   │   └── UsageStatsDataSource.kt
│   │   ├── cache/              # Caching
│   │   │   └── UsageDataCache.kt
│   │   ├── mapper/             # Converters
│   │   │   ├── UsageStatsMapper.kt
│   │   │   ├── SessionMapper.kt
│   │   │   └── SpiritualMapper.kt
│   │   └── repository/         # Implementations
│   │       ├── UsageRepositoryImpl.kt
│   │       ├── FocusSessionRepositoryImpl.kt
│   │       └── SpiritualRepositoryImpl.kt
│   │
│   ├── service/                # Android Services
│   │   └── FocusLockService.kt
│   │
│   ├── di/                     # Dependency Injection
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   └── ServiceModule.kt
│   │
│   ├── MainActivity.kt
│   └── RupturaApplication.kt
│
├── app/build.gradle.kts        # App dependencies
├── build.gradle.kts            # Project configuration
├── settings.gradle.kts
├── CONTRIBUTING.md             # Contribution guide
├── LICENSE                     # Apache 2.0
└── README.md                   # This file
```

---

## Building

### Via Android Studio

1. Build > Make Project
2. Or press `Ctrl+F9` (Linux/Windows) / `Cmd+F9` (Mac)

### Via Command Line

#### Build Debug APK
```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### Build Release APK (Signed)
```bash
./gradlew assembleRelease
```

#### Install via ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Clean Build
```bash
./gradlew clean
```

---

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumentation Tests (Requires device)
```bash
./gradlew connectedAndroidTest
```

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```

---

<a name="troubleshooting-en"></a>
## Troubleshooting

### "No data available" on Dashboard

**Problem**: The app doesn't show usage data.

**Solutions**:
1. Verify that `PACKAGE_USAGE_STATS` permission is actually granted
2. Use the phone normally for a few minutes then open the app
3. Try changing period to "7 Days" or "30 Days"
4. Pull-to-refresh on main screen
5. Force stop the app and open again

### App won't compile

**Problem**: Gradle build errors.

**Solutions**:
1. File > Invalidate Caches > Invalidate and Restart
2. Clean build: `./gradlew clean`
3. Check internet connection (Gradle needs to download dependencies)
4. Update Android Studio to latest version
5. Verify JDK 17 is installed

### Permission not detected

**Problem**: Even after granting permission, app stays on permission screen.

**Solutions**:
1. Force stop the app and open again
2. Check in Settings > Apps > Ruptura > Permissions
3. Revoke and grant permission again
4. Restart device

### Lock screen doesn't appear

**Problem**: Focus session started but lock screen doesn't show.

**Solutions**:
1. Verify that `SYSTEM_ALERT_WINDOW` permission is granted
2. Settings > Apps > Special access > Display over other apps > Ruptura (Active)
3. Some custom launchers may block overlays
4. Try using Android's default launcher

### App closes by itself during focus session

**Problem**: The foreground service is terminated by the system.

**Solutions**:
1. Disable battery optimization for Ruptura
2. Settings > Battery > Battery optimization > Ruptura > Don't optimize
3. Some manufacturers (Xiaomi, Huawei) have aggressive battery managers
4. Add Ruptura to protected/auto-start apps list

---

<a name="roadmap-en"></a>
## Roadmap

Features planned for future versions:

- [ ] **Selective app blocking** - Block specific apps after time limit
- [ ] **Custom goals** - Set daily limits for individual apps
- [ ] **Smart notifications** - Alerts when exceeding configured limits
- [ ] **Weekly/monthly reports** - Trend analysis and progress
- [ ] **Home screen widget** - Usage summary and quick access to sessions
- [ ] **Gamification** - Achievements, streaks, and reward system
- [ ] **Cloud sync** - Backup and sync across devices
- [ ] **Family mode** - Parental monitoring and limits for children
- [ ] **Calendar integration** - Automatic blocking during events
- [ ] **Data export** - CSV, JSON for external analysis
- [ ] **More spiritual activities** - Expandable library of practices
- [ ] **Spiritual activity reminders** - Customizable notifications
- [ ] **Spiritual journal** - Record reflections and progress

---

<a name="contributing-en"></a>
## Contributing

Contributions are very welcome! This project is open-source and was created to help the community.

To contribute:

1. Fork the project
2. Create a branch for your feature (`git checkout -b feature/MyFeature`)
3. Commit your changes (`git commit -m 'Add MyFeature'`)
4. Push to the branch (`git push origin feature/MyFeature`)
5. Open a Pull Request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and contribution process.

---

<a name="license-en"></a>
## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

```
Copyright 2024 Ricardo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<a name="author-en"></a>
## Author

**Ricardo**

Developer focused on **digital wellness** and **conscious technology**.

### Personal Mission

We are the first generation to use smartphones. We didn't choose to be born in this era of constant digital distraction, but we can choose how to respond to it.

This app was born from my own struggle against digital addiction. I realized that, just as cars needed seatbelts, we need tools to navigate the digital age safely.

**Ruptura** is not just about productivity - it's about recovering your attention, your time, and your spiritual life. It's about creating space for what truly matters.

I hope this app helps you as much as it has helped me. If you also believe we can use technology more consciously and healthily, consider contributing to the project or sharing it with people who might benefit.

**Interior Combat** (Combate Interior) is a daily journey. Let's walk together.

---

**Contact**: [Your Email or GitHub]

**Status**: ✅ Functional MVP - Version 1.0

---

**Made with focus on digital wellness and spiritual growth** 🙏
