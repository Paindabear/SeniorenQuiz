# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

Build and install via Gradle wrapper (run from repo root):

```bash
# Debug build
./gradlew assembleDebug

# Release build (uses debug signing config — see note below)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.example.seniorenquiz.ExampleUnitTest"
```

> The release build is intentionally signed with the debug key (`signingConfig = signingConfigs.getByName("debug")`) to allow direct APK installation on devices without Play Store.

## Architecture Overview

**Single Activity + Fragments (MVVM)**

- `MainActivity` — hosts the `BottomNavigationView` and swaps Fragments into `R.id.fragment_container`. Also runs the update check on startup.
- `QuizActivity` — the active quiz screen; uses ViewBinding and holds a `QuizViewModel` via `by viewModels()`.
- `QuizViewModel` — holds quiz state (question list, current index, score, joker used). Calls `QuizRepository` to load questions. The joker resets **per question** (not per game).

**Data flow**

```
QuizApplication.onCreate()
  └─ QuizRepository.updateQuestionsFromUrl()   ← background thread, updates questions.json from GitHub

QuizRepository.getQuestions(context, mode)
  ├─ IMAGE / AUDIO mode → loads images.json / audio.json from assets (no OTA update)
  └─ TEXT / FAIRYTALE mode → checks filesDir/questions.json first, falls back to bundled assets/questions.json
```

**Quiz Modes** (`QuizMode` enum)

| Mode | Data source | Category filter |
|------|------------|-----------------|
| `TEXT` | `questions.json` | excludes `GRIMM_*` categories |
| `FAIRYTALE` | `questions.json` | only `GRIMM_*` categories |
| `IMAGE` | `images.json` | `IMAGE_MIX` → all; otherwise exact category match |
| `AUDIO` | `audio.json` | all (no category filter currently) |

**Launching a quiz**: Fragments start `QuizActivity` with two Intent extras:
- `"CATEGORY"` — e.g. `"ALL"`, `"NATURE"`, `"GRIMM_ALL"`, `"IMAGE_MIX"`
- `"QUIZ_MODE"` — `QuizMode.name` string (defaults to `"TEXT"` if absent)

**Question model** (`Question` data class in `QuizData.kt`):
```kotlin
data class Question(
    val text: String,
    val answers: List<String>,   // always 3 answers
    val correctAnswerIndex: Int,
    val category: String,
    val imagePath: String? = null,   // e.g. "images/animal_01.jpg"
    val audioPath: String? = null    // e.g. "audio/instrument_01.mp3"
)
```
Asset paths in JSON use the format `"assets/..."` — the code strips the `assets/` prefix before calling `context.assets.open()`.

## Update System

Two independent update mechanisms:

1. **Questions-only update** (`QuizApplication` → `QuizRepository.updateQuestionsFromUrl`): downloads `questions.json` from GitHub on every app start, saves to `filesDir`. Image/audio JSON files are **not** updated this way.

2. **Full APK update** (`MainActivity.checkForAppUpdate`): fetches `version.json` from GitHub, compares `versionCode` with `BuildConfig.VERSION_CODE`. When a new version is available, downloads and installs the APK in-app. When releasing a new version, **both** `version.json` (at repo root) and `versionCode`/`versionName` in `app/build.gradle.kts` must be bumped.

## Adding Questions

- **Text questions**: edit `app/src/main/assets/questions.json`. The schema matches the `Question` data class (no `imagePath`/`audioPath` needed for text-only questions). The `category` field must match one of the known category codes (`NATURE`, `HISTORY`, `GEOGRAPHY`, `MUSIC`, `TV`, `SPACE`, `FOOD`, `SPORT`, `PROVERBS`, `GRIMM_*`).
- **Image questions**: edit `app/src/main/assets/images.json`; place image files in `app/src/main/assets/images/`.
- **Audio questions**: edit `app/src/main/assets/audio.json`; place audio files in `app/src/main/assets/audio/`.
- All categories must have exactly 3 answer options per question.
