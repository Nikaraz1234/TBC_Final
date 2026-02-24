# 📱 AxisSolve

AxisSolve is a modern, multi-module **Android Trivia Game application** built using Kotlin and Jetpack Compose.  
The app allows users to test their knowledge across different categories, compete on leaderboards, unlock achievements, and track their performance over time.

It is designed using Clean Architecture principles and a feature-based modular structure to ensure scalability, maintainability, and testability.

---

## 🎮 About the Game

AxisSolve is a **general knowledge trivia game** where users:

- Answer quiz questions across multiple categories
- Compete in different game modes (e.g., Daily, Weekly, All-Time)
- Earn points based on performance
- Climb leaderboards
- Unlock achievements
- Manage their profile and track statistics
- Receive real-time notifications

The game integrates Firebase services for authentication, cloud data storage, and push notifications.

The application demonstrates production-level Android development practices including modularization, state management, dependency injection, and reactive programming with Kotlin Flows.

## 🚀 Overview

**AxisSolve** is a feature-based modular Android application structured for scalability, maintainability, and testability.

The project follows modern Android architecture guidelines and includes authentication, game logic, leaderboard management, profile handling, achievements system, and push notifications.

This repository is structured as a portfolio-level Android project.

---

## 🏗 Architecture

The application follows:

- **Clean Architecture**
- **MVVM pattern**
- **Feature-based modularization**
- **Unidirectional state management**
- **Repository pattern**
- **UseCase abstraction layer**
- **BaseViewModel architecture**
- **Dependency Injection with Hilt**

### 📦 Module Structure

```
TBC_Final/
│
├── app/                         (Application entry point)
│
├── core/
│   ├── domain/                  (Business logic & models)
│   ├── data/                    (Repository implementations)
│   └── presentation/            (BaseViewModel & common logic)
│
├── coreUi/                      (Shared UI components)
│
├── feature/
│   ├── splash/
│   ├── welcome/
│   ├── login/
│   ├── register/
│   ├── main/
│   ├── game/
│   │   └── archive/
│   ├── leaderboard/
│   ├── profile/
│   │   ├── profilePage/
│   │   └── editProfile/
│   ├── notification/
│   └── achievements/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

Each feature is isolated in its own Gradle module to improve scalability and maintain clear separation of concerns.

## 🛠 Tech Stack

### 🧠 Language
- Kotlin

### 🎨 UI
- Jetpack Compose
- Material 3
- Navigation Compose

### 💉 Dependency Injection
- Hilt

### 🔥 Backend / Services
- Firebase Authentication
- Firebase Firestore
- Firebase Cloud Messaging

### 💾 Data Persistence
- DataStore Preferences

### 🔄 Async & Reactive
- Kotlin Coroutines
- StateFlow / SharedFlow

### 🧪 Testing
- JUnit
- MockK
- Turbine (Flow testing)
- Compose UI Testing
- Coroutine Test Dispatchers

---

## 📱 Core Features

### 🔐 Authentication
- Email & password login
- Validation use cases
- Firebase authentication integration
- Persistent session using DataStore

### 🎮 Game Module
- Dynamic game modes
- Delegate-based game logic separation
- Connectivity observer integration
- Score tracking & state management

### 🏆 Leaderboard
- Filter by category
- Mode switching (Daily / Weekly / All Time)
- Reactive updates via Flow
- Clean state management

### 👤 Profile
- Observe current user
- Logout functionality
- Delete account feature
- Edit profile capability

### 🏅 Achievements
- Achievement unlocking logic
- Domain-driven validation
- Stats-based reward system

### 🔔 Notifications
- Firebase Cloud Messaging integration
- Real-time push notification handling

---

## 🧩 Design Principles

- Modular first approach
- Clear dependency direction (Feature → Core)
- Strong separation between UI and business logic
- Testable ViewModels
- Reusable UI components via coreUi module
- Version catalog dependency management

---

## 🧪 Running Tests

Run unit tests:

```bash
./gradlew test
