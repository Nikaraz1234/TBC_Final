<div align="center">

# AxisSolve

**A production-grade, multi-category trivia game for Android**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-8.11.2-brightgreen?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/build)
[![Firebase](https://img.shields.io/badge/Firebase-33.0.0-FFCA28?style=flat-square&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Hilt](https://img.shields.io/badge/Hilt-2.57.2-2196F3?style=flat-square)](https://dagger.dev/hilt)
[![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)](LICENSE)

</div>

---

## Overview

AxisSolve is a feature-rich Android trivia game that challenges players across **Movies**, **Comics**, **Books**, and **Games** categories. Built to portfolio-level standards, it showcases production patterns including Clean Architecture, delegate-based game logic, Firebase integration, and a fully modularised Gradle build with convention plugins.

Players answer questions, earn XP, climb leaderboards, unlock achievements, and track daily goals — all in a smooth, Compose-first UI.

---

## Screenshots

> _Add screenshots / GIFs of the app here_

---

## Features

| Area | Highlights |
|---|---|
| **Authentication** | Email/password login & registration, Google Sign-In, persistent session via DataStore |
| **Game Engine** | 10+ game modes across 4 categories; delegate pattern separates each mode's logic |
| **Leaderboard** | Daily / Weekly / All-Time filters, category switching, real-time Firestore updates |
| **Achievements** | Condition evaluator driven by live game stats; unlocks stored in Firestore |
| **Daily Goals** | Atomic DataStore updates, auto-reset at midnight, progress tracking per mode |
| **Profile** | View & edit profile, logout, delete account |
| **Notifications** | Firebase Cloud Messaging integration for push notifications |

---

## Game Modes

### 🎬 Movies
| Mode | Description |
|---|---|
| **Cover** | Identify the film from a progressively revealed poster grid |
| **Plot** | Guess the movie from a cryptic plot description — timed |
| **Emoji** | Decode an emoji sequence that represents the title |

### 📚 Comics / Manga
| Mode | Description |
|---|---|
| **Manga Rating** | Rank pairs of manga by community score |
| **Rankle** | Wordle-style manga guessing with directional feedback |
| **Emoji** | Common emoji-guessing mode applied to the comics category |

### 📖 Books & Novels
| Mode | Description |
|---|---|
| **Synopsis** | Choose the correct synopsis from four options; hints remove wrong answers |
| **Odd One Out** | Find the book that doesn't share the trait of the other three |
| **Book By Order** | Arrange books in the correct series / publication order |

### 🎮 Video Games
| Mode | Description |
|---|---|
| **Achievement** | Guess the game from one of its real in-game achievements |
| **Description** | Identify the game from a short textual description |
| **Screenshot** | Name the game shown in a cropped or blurred screenshot |

---

## Architecture

AxisSolve follows **Clean Architecture** with a strict **Feature → Core** dependency direction and a **delegate-based game engine** that keeps each mode's logic in isolation.

```
┌──────────────────────────────────────────────┐
│                   :app                        │  Navigation host, DI root
└──────────────────────┬───────────────────────┘
                       │ depends on
         ┌─────────────▼──────────────────────────────────────┐
         │              :feature/*                             │
         │  splash · welcome · login · register · main · game  │
         │  leaderboard · profile · achievements · notification│
         └──────────┬───────────────────────────┬─────────────┘
                    │                           │
         ┌──────────▼──────────┐    ┌───────────▼────────────┐
         │    :core/domain     │    │    :core/presentation   │
         │  Use cases · Models │    │  BaseViewModel · common │
         │  Repositories (i/f) │    └────────────────────────┘
         └──────────┬──────────┘
                    │
         ┌──────────▼──────────┐    ┌──────────────────┐
         │    :core/data       │    │    :core-ui       │
         │  Repo impls · DI    │    │  Shared Compose   │
         │  Firebase · Retrofit│    │  components       │
         └─────────────────────┘    └──────────────────┘
```

### Key patterns

- **MVVM + Unidirectional Data Flow** — every screen has a `Contract` defining `State`, `Event`, and `Effect`
- **Delegate pattern** — `GameModeDelegate` interface; one delegate per game mode, wired by `GameDelegateFactory`
- **Map Multibinding** — repositories injected as `Map<String, Repository>` keyed by category (`"MOVIES"`, `"BOOKS"`, …)
- **Convention plugins** — all Gradle config lives in `build-logic/`; feature modules get a full stack (Compose + Hilt + Navigation + Serialization) from a single `mycomposeapp.android.feature` plugin

---

## Module Structure

```
TBC_Final/
├── app/                              # App entry point & root navigation
│
├── build-logic/
│   └── convention/                   # Custom Gradle convention plugins
│       ├── mycomposeapp.android.application
│       ├── mycomposeapp.android.application.compose
│       ├── mycomposeapp.android.feature
│       ├── mycomposeapp.android.hilt
│       ├── mycomposeapp.android.library
│       ├── mycomposeapp.android.library.compose
│       └── mycomposeapp.kotlin.jvm
│
├── core/
│   ├── domain/                       # Pure JVM — models, use cases, repo interfaces
│   ├── data/                         # Android — repo implementations, Firebase, Retrofit, DI
│   └── presentation/                 # BaseViewModel, shared presentation utilities
│
├── core-ui/                          # Shared Compose components & theme
│
├── feature/
│   ├── splash/
│   ├── welcome/
│   ├── login/
│   ├── register/
│   ├── main/                         # Dashboard / home
│   ├── game/
│   │   ├── domain/                   # Game models, repo interfaces, use cases
│   │   ├── data/                     # API services, mappers, repo implementations
│   │   ├── presentation/             # GameViewModel, GameContract, delegates, screens
│   │   └── archive/                  # Completed-game history
│   ├── leaderboard/
│   ├── profile/
│   │   ├── profile_page/
│   │   └── edit_profile/
│   ├── achievements/
│   └── notification/
│
├── test-utils/                       # Shared test helpers & fakes
└── gradle/
    └── libs.versions.toml            # Version catalog
```

---

## Tech Stack

### Language & Build
| Tool | Version |
|---|---|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.11.2 |
| KSP | 2.0.21-1.0.25 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 |

### UI
| Library | Notes |
|---|---|
| Jetpack Compose BOM | 2024.09.00 |
| Material 3 | Custom `AppColorScheme` theme |
| Navigation Compose | 2.9.6 |
| Coil | 2.7.0 — async image loading |

### Dependency Injection
| Library | Version |
|---|---|
| Hilt | 2.57.2 |
| Hilt Navigation Compose | 1.2.0 |

### Networking
| Library | Version |
|---|---|
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Kotlinx Serialization Converter | 1.0.0 |
| TMDB API | Movie covers & plot data |
| Google Books API | Book synopsis & metadata |

### Backend / Services
| Service | Usage |
|---|---|
| Firebase Authentication | Email/password & Google Sign-In |
| Firebase Firestore | User data, leaderboard, achievements |
| Firebase Cloud Messaging | Push notifications |

### Async & State
| Library | Notes |
|---|---|
| Kotlin Coroutines | 1.7.3 |
| StateFlow / SharedFlow | ViewModel state & one-shot effects |
| DataStore Preferences | Session & daily goals persistence |
| kotlinx-datetime | 0.6.1 — daily reset logic |

### Testing
| Library | Version |
|---|---|
| JUnit 4 | 4.13.2 |
| MockK | 1.13.12 |
| Turbine | 1.1.0 — Flow assertions |
| Espresso | 3.7.0 |
| Coroutines Test | — |

---

## Getting Started

### Prerequisites
- Android Studio Meerkat or later
- JDK 11+
- A Firebase project with Authentication, Firestore, and Cloud Messaging enabled
- TMDB API key
- Google Books API key

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/AxisSolve.git
   cd AxisSolve
   ```

2. **Add `google-services.json`**

   Download from your Firebase console and place it in the `app/` directory.

3. **Configure API keys**

   Create (or edit) `local.properties` in the project root:
   ```properties
   TMDB_API_KEY=your_tmdb_api_key_here
   GOOGLE_BOOKS_API_KEY=your_google_books_api_key_here
   ```

4. **Sync & build**
   ```bash
   ./gradlew assembleDebug
   ```

---

## Running Tests

```bash
# All unit tests
./gradlew test

# Tests for a specific module
./gradlew :feature:game:domain:test
./gradlew :core:domain:test

# Android instrumented tests
./gradlew connectedAndroidTest
```

---

## Project Conventions

- **Dependency direction**: Feature modules only depend on Core; Core never depends on Feature
- **State management**: Each screen owns a `Contract` file with sealed `State`, `Event`, and `Effect` classes
- **`collectAsStateWithLifecycle()`** is used everywhere instead of `collectAsState()`
- **`Resource<T>`** sealed class wraps all async results from the data layer
- **Game stats keys** are always built via `GameModeIds.statsKey(categoryType, gameModeId)` — never hardcoded strings
- **API keys** live only in `local.properties`, exposed as `BuildConfig` fields — never committed

---

## License

```
MIT License

Copyright (c) 2026 AxisSolve

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```
