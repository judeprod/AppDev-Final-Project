# 💧 Save Our Water - Android Water Conservation Tracker

![App Banner](https://img.shields.io/badge/Platform-Android-green.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-21-blue.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-blue.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Status](https://img.shields.io/badge/Status-Active%20Development-orange.svg)

**Turning every drop count into a global movement.**

Save Our Water is an innovative Android application that empowers users to track, reduce, and contextualize their water footprint through gamification, real-time tracking, and impact visualization.

---

## 🌊 Project Overview

### 💡 The Problem
We live in a world where we track everything—steps, calories, screen time—yet we completely ignore the most finite resource that keeps us alive: **Water**. Most people turn on the tap assuming the flow is endless, without accountability for consumption.

### ✨ The Solution
**Save Our Water** transforms passive water consumption into active environmental stewardship through:
- 💧 **Real-time water usage tracking**
- 🎯 **Gamified conservation goals**
- 📊 **Impact visualization & contextual insights**
- 🏆 **Achievement system** for behavioral change
- 🌍 **Educational content** on water conservation

---

## 🚀 Features

### 📱 Core Functionality
- **🚿 Activity-based tracking** - Log showers, taps, toilet flushes, laundry, dishes, garden watering
- **🧮 Automatic calculations** - Smart water usage estimates based on activity type and duration
- **🎯 Daily goals & progress** - Set conservation targets and track real-time performance
- **📴 Offline-first** - Works without internet, syncs when connected via Supabase
- **🔄 Real-time updates** - LiveData & StateFlow ensure instant UI updates

### 🎮 Gamification
- **🏅 Achievement system** - Unlock badges for streaks, milestones, and efficient usage
- **🔥 Streak tracking** - Build daily conservation habits with visual streak counters
- **📈 Progress visualization** - Beautiful charts and graphs with MPAndroidChart
- **🔔 Notifications** - Celebrate achievements and receive daily reminders
- **🥇 Tier system** - Bronze, Silver, Gold, and Platinum achievements

### 🌎 Value Awareness
- **💬 Contextual insights** - "You saved enough water to sustain a family for a week!"
- **🌐 Real-world comparisons** - Translate liters into tangible equivalents
- **📚 Educational content** - Water facts, conservation tips, best practices
- **🧮 Impact calculator** - Project savings from behavioral changes
- **📊 Weekly trends** - Visual breakdown of usage patterns

---

## 🛠️ Technology Stack

### 📱 Android Development
- **Language:** Kotlin 1.9.21
- **Min SDK:** API 21 (Android 5.0 Lollipop) - 97%+ device coverage
- **Target SDK:** API 34 (Android 14)
- **IDE:** Android Studio (latest stable)
- **Build Tool:** Gradle 8.2.1 with Kotlin DSL

### 🏗️ Architecture
- **Pattern:** MVVM (Model-View-ViewModel)
- **Architecture Components:**
  - 🧩 **ViewModel** - Lifecycle-aware UI state management
  - 📡 **LiveData & StateFlow** - Observable data holders with reactive updates
  - 🗄️ **Room Database** - Local SQLite abstraction (v2.6.1)
  - 🧭 **Navigation Component** - Fragment navigation with SafeArgs (v2.7.6)
  - ⚙️ **WorkManager** - Background sync scheduling (v2.9.0)
  - 🔗 **Repository Pattern** - Single source of truth for data

### 📚 Libraries & Dependencies
- **🎨 UI:** Material Design 3 (v1.11.0), MotionLayout, ViewBinding
- **📊 Charts:** MPAndroidChart (v3.1.0)
- **🌐 Networking:** Retrofit 2.9.0 + OkHttp 4.12.0
- **🗄️ Database:** Room 2.6.1 + Supabase (cloud sync)
- **⚡ Async:** Kotlin Coroutines 1.7.3 + Flow
- **🖼️ Image Loading:** Coil 2.5.0
- **💾 Preferences:** DataStore 1.0.0
- **🧪 Testing:** JUnit 5, Espresso 3.5.1, MockK 1.13.8

### ☁️ Backend & Cloud
- **Database:** Supabase (PostgreSQL-based)
- **Authentication:** Supabase Auth (email/password, social logins)
- **API:** RESTful with Row Level Security
- **Real-time:** Supabase Realtime subscriptions
- **Hosting:** Supabase free tier (500MB DB, 2GB bandwidth/month)

---

## 📁 Project Structure

```
SaveOurWater/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/saveourwater/
│   │   │   │   ├── SaveOurWaterApplication.kt       # App initialization
│   │   │   │   ├── ui/                              # 🎨 UI layer (Fragments, Activities)
│   │   │   │   │   ├── main/                        # MainActivity & Navigation
│   │   │   │   │   ├── home/                        # 🏠 Dashboard Fragment
│   │   │   │   │   │   ├── HomeFragment.kt
│   │   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   │   └── RecentActivitiesAdapter.kt
│   │   │   │   │   ├── tracking/                    # 📊 Water Tracking
│   │   │   │   │   │   ├── TrackingFragment.kt
│   │   │   │   │   │   ├── TrackingViewModel.kt
│   │   │   │   │   │   └── ActivityAdapter.kt
│   │   │   │   │   ├── achievements/                # 🏆 Achievements (Phase 3)
│   │   │   │   │   ├── insights/                    # 💡 Insights (Phase 4)
│   │   │   │   │   └── onboarding/                  # 👋 Onboarding (Phase 5)
│   │   │   │   ├── data/                            # 🗂️ Data layer
│   │   │   │   │   ├── local/                       # Room Database
│   │   │   │   │   │   ├── dao/                     # Data Access Objects
│   │   │   │   │   │   │   ├── WaterActivityDao.kt
│   │   │   │   │   │   │   └── EcoGoalDao.kt
│   │   │   │   │   │   ├── entities/                # Database entities
│   │   │   │   │   │   │   ├── WaterActivity.kt
│   │   │   │   │   │   │   └── EcoGoal.kt
│   │   │   │   │   │   └── database/                # Database setup
│   │   │   │   │   │       ├── AppDatabase.kt
│   │   │   │   │   │       └── Converters.kt
│   │   │   │   │   ├── remote/                      # Supabase API
│   │   │   │   │   │   └── api/
│   │   │   │   │   │       └── SupabaseApi.kt
│   │   │   │   │   └── repository/                  # Repository pattern
│   │   │   │   │       └── WaterRepository.kt
│   │   │   │   └── utils/                           # ⚙️ Utilities
│   │   │   │       └── WaterCalculator.kt
│   │   │   ├── res/                                 # 🎨 Resources
│   │   │   │   ├── layout/                          # XML layouts (9 files)
│   │   │   │   ├── drawable/                        # Vector icons (13 files)
│   │   │   │   ├── color/                           # Color selectors
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml                   # Color palette (73 colors)
│   │   │   │   │   ├── strings.xml                  # All UI text (123 strings)
│   │   │   │   │   └── themes.xml                   # Material Design 3 themes
│   │   │   │   ├── navigation/                      # Navigation graph
│   │   │   │   └── menu/                            # Bottom navigation menu
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                    # 🧪 Unit tests
│   │   └── androidTest/                             # 📱 Instrumentation tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

### 📊 Current Implementation Stats
- **Total Files:** 45 source files
- **Kotlin Files:** 19 classes
- **XML Resources:** 26 files
- **Vector Drawables:** 13 custom icons
- **Lines of Code:** ~3,500+ lines
- **Architecture:** 100% MVVM with Repository pattern

---

## 🎯 Development Timeline

**Duration:** January 23 - February 13, 2026 (3 weeks)

### ✅ Phase 1: Foundation (Jan 23-25) - **COMPLETED**
- ✅ Project setup & Gradle configuration
- ✅ Database schema (Room entities: WaterActivity, EcoGoal)
- ✅ Supabase configuration
- ✅ Material Design 3 theming (73 colors, custom styles)
- ✅ Application class with notification channels

### ✅ Phase 2: Core Tracking (Jan 26-30) - **COMPLETED**
- ✅ MainActivity with Bottom Navigation
- ✅ TrackingFragment with activity grid (6 activities)
- ✅ TrackingViewModel with LiveData/StateFlow
- ✅ WaterRepository (offline-first with sync)
- ✅ WaterCalculator utility (usage estimation)
- ✅ HomeFragment dashboard with stats cards
- ✅ HomeViewModel with insights generation
- ✅ RecentActivitiesAdapter with relative time
- ✅ Navigation graph with 4 destinations
- ✅ 13 custom vector icons (activities + navigation)

### 🚧 Phase 3: Gamification (Jan 31 - Feb 4) - **IN PROGRESS**
- ⏳ Achievement system implementation
- ⏳ Streak tracking logic
- ⏳ Notifications for achievements
- ⏳ AchievementsFragment UI

### ⏳ Phase 4: Value Awareness (Feb 5-7) - **PENDING**
- ⏳ Insights generation engine
- ⏳ Educational content database
- ⏳ Impact calculator
- ⏳ InsightsFragment UI

### ⏳ Phase 5: UI Polish (Feb 8-11) - **PENDING**
- ⏳ MotionLayout animations
- ⏳ Dark mode support
- ⏳ Onboarding flow
- ⏳ Edge-to-edge display
- ⏳ Lottie animations for achievements

### ⏳ Phase 6: Testing & Deployment (Feb 12-13) - **PENDING**
- ⏳ Unit test suite (JUnit + MockK)
- ⏳ UI tests (Espresso)
- ⏳ APK generation with ProGuard
- ⏳ Play Store assets preparation

---

## 🔧 Setup Instructions

### 📋 Prerequisites
- ☕ **Android Studio:** Latest stable version (Hedgehog or newer)
- ☕ **JDK:** Version 17+
- 📱 **Android SDK:** API 21+ (automatically installed by Studio)
- 🔧 **Git:** For version control

### 🚀 Installation

1. **Clone the repository**
```bash
git clone https://github.com/judeprod/AppDev-Final-Project.git
cd AppDev-Final-Project
```

2. **Open in Android Studio**
- File → Open → Select project directory
- Wait for Gradle sync to complete (may take 2-5 minutes)
- If prompted, accept Android SDK licenses

3. **Configure Supabase** (Optional - for cloud sync)
- Create a [Supabase](https://supabase.com) account (free tier)
- Create a new project
- Copy your **Project URL** and **Anon Key** from Settings → API
- Update `gradle.properties`:
```properties
supabase.anon.key=YOUR_SUPABASE_ANON_KEY_HERE
```

4. **Build and Run**
- Select an emulator (API 21+) or connected physical device
- Click **Run** (▶️) or press `Shift + F10`
- App should launch on your device/emulator

### ⚠️ Troubleshooting
- **Gradle sync fails:** Ensure you have JDK 17+ and stable internet
- **Build errors:** Clean project with `Build → Clean Project`, then rebuild
- **Missing dependencies:** Invalidate caches with `File → Invalidate Caches → Invalidate and Restart`

---

## 🧪 Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Instrumentation Tests
```bash
# Run UI tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.saveourwater.ExampleTest
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

---

## 📱 Building APK

### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build (Signed)
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
# Note: Requires keystore configuration in gradle.properties
```

### Install on Device
```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📝 Changelog

### Version 1.0.0-beta (January 24, 2026)

#### 🎉 Phase 2 Implementation - Core Tracking Features

**Added:**
- ✨ **MainActivity** with Material Design 3 bottom navigation
- ✨ **TrackingFragment** with 6 activity types (Shower, Tap, Toilet, Laundry, Dishes, Garden)
- ✨ **TrackingViewModel** with reactive LiveData/StateFlow for real-time updates
- ✨ **HomeFragment** dashboard with today's usage, streak counter, and goal progress
- ✨ **HomeViewModel** with contextual insights generation
- ✨ **WaterRepository** implementing offline-first pattern with Room + Supabase sync
- ✨ **WaterCalculator** utility with smart usage estimation (10 L/min shower, 7 L/min tap, etc.)
- ✨ **RecentActivitiesAdapter** with relative time display ("2 hours ago", "Just now")
- ✨ **13 custom vector icons** for activities and navigation
- ✨ **Navigation graph** with SafeArgs for type-safe navigation
- ✨ **Weekly usage chart** placeholder (MPAndroidChart integration)

**Database:**
- 🗄️ Created **WaterActivity** entity with 8 fields (id, activityType, litersUsed, durationSeconds, timestamp, notes, syncedToCloud, cloudId)
- 🗄️ Created **EcoGoal** entity for daily water limits
- 🗄️ Implemented **WaterActivityDao** with 10 queries including Flow-based reactive queries
- 🗄️ Implemented **EcoGoalDao** for goal management
- 🗄️ Created **AppDatabase** singleton with TypeConverters for ActivityType enum

**UI/UX:**
- 🎨 Comprehensive **color palette** with 73 colors (primary blue gradient, conservation green, achievement tiers)
- 🎨 **Material Design 3 themes** with custom card, button, and text styles
- 🎨 **Bottom navigation selector** with color states
- 🎨 **Splash screen background** with water blue gradient
- 🎨 **9 XML layouts** including responsive grids and cards

**Resources:**
- 📝 **123 string resources** for complete UI text
- 🎨 **13 vector drawables** (ic_shower, ic_tap, ic_toilet, ic_laundry, ic_dishes, ic_garden, ic_custom, ic_home, ic_track, ic_achievements, ic_insights, ic_water_drop)
- 🔧 **gradle.properties** with AndroidX and build optimizations

**Technical Improvements:**
- ⚡ Kotlin Coroutines for async operations
- ⚡ StateFlow for reactive UI updates
- ⚡ ViewBinding enabled for null-safe view access
- ⚡ Repository pattern for clean architecture
- ⚡ Offline-first data strategy

**Fixed:**
- 🐛 Missing drawable resources causing build failures
- 🐛 Bottom navigation icon references
- 🐛 Gradle configuration for KSP and Navigation SafeArgs

---

### Version 0.1.0-alpha (January 23-25, 2026)

#### 🎊 Phase 1 Implementation - Foundation & Architecture

**Added:**
- ✨ Initial project setup with Kotlin 1.9.21 and AGP 8.2.1
- ✨ MVVM architecture foundation with Android Architecture Components
- ✨ Room Database configuration (v2.6.1)
- ✨ Retrofit setup for Supabase API integration
- ✨ Material Design 3 theming system
- ✨ SaveOurWaterApplication class with notification channels
- ✨ AndroidManifest with permissions (INTERNET, ACCESS_NETWORK_STATE, POST_NOTIFICATIONS, VIBRATE)
- ✨ Comprehensive string resources (123 strings)
- ✨ Color system with water blue and conservation green palettes
- ✨ ProGuard rules for release builds

**Dependencies Added:**
- 📦 AndroidX Core KTX 1.12.0
- 📦 Material Components 1.11.0
- 📦 Navigation Component 2.7.6
- 📦 Room Database 2.6.1
- 📦 Retrofit 2.9.0 + OkHttp 4.12.0
- 📦 Kotlin Coroutines 1.7.3
- 📦 MPAndroidChart 3.1.0
- 📦 Coil 2.5.0
- 📦 DataStore 1.0.0
- 📦 WorkManager 2.9.0

**Infrastructure:**
- 🔧 Git repository initialized
- 🔧 GitHub remote configured (https://github.com/judeprod/AppDev-Final-Project)
- 🔧 Gradle build system with Kotlin DSL
- 🔧 Multi-module architecture prepared

---

### Version 0.3.0-alpha (January 29, 2026)

#### 🎊 Phase 3 Implementation - Coming Soon

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. 💾 Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. 📤 Push to the branch (`git push origin feature/AmazingFeature`)
5. 🔀 Open a Pull Request

### 📏 Code Style Guidelines
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and under 20 lines when possible
- Write unit tests for new features

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact

**🎓 Project Team:**
- �‍💻 **[Jay Arre Talosig](https://github.com/flexycode)** - Machine Learning Engineer | Blockchain Developer    
- �‍💻 **[Jude Renwell B. Prodigalidad](https://github.com/judeprod)** - Machine Learning Engineer | Mobile App Developer | Software Engineer 
- �‍💻 **[Diana Grace Paray](https://github.com/)** - Digital Forensics Analyst | Software Engineer | UI/UX Designer
- 👨‍💻 **[Kris Brian Diaz](https://github.com/)** - Digital Forensics Analyst | Software Engineer | UI/UX Designer

**🔗 Links:**
- 📦 Project Repository: [https://github.com/judeprod/AppDev-Final-Project](https://github.com/judeprod/AppDev-Final-Project)
- 📚 Documentation: Coming soon
- 🐛 Issue Tracker: [GitHub Issues](https://github.com/judeprod/AppDev-Final-Project/issues)

---

## 🙏 Acknowledgments

- 🌍 **Inspiration:** The global water crisis and the need for individual accountability
- 🎨 **Design:** [Material Design 3](https://m3.material.io/) guidelines by Google
- 📚 **Libraries:** Android Jetpack, Supabase, MPAndroidChart community
- 🎯 **Icons:** Material Icons and custom vector designs
- 💡 **Architecture:** [Android App Architecture Guide](https://developer.android.com/topic/architecture)

---

## 🌟 Project Statistics

![GitHub repo size](https://img.shields.io/github/repo-size/judeprod/AppDev-Final-Project)
![GitHub code size](https://img.shields.io/github/languages/code-size/judeprod/AppDev-Final-Project)
![GitHub contributors](https://img.shields.io/github/contributors/judeprod/AppDev-Final-Project)
![GitHub last commit](https://img.shields.io/github/last-commit/judeprod/AppDev-Final-Project)

---

**💧 Making water visible, valuable, and sustainable - one drop at a time. 🌍**

*Built with ❤️ for a better planet*
