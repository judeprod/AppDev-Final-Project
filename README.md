# Save Our Water - Android Water Conservation Tracker

![App Banner](https://img.shields.io/badge/Platform-Android-green.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-21-blue.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-blue.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)

**Turning every drop count into a global movement.**

Save Our Water is an innovative Android application that empowers users to track, reduce, and contextualize their water footprint through gamification, real-time tracking, and impact visualization.

---

## 🌊 Project Overview

### The Problem
We live in a world where we track everything—steps, calories, screen time—yet we completely ignore the most finite resource that keeps us alive: **Water**. Most people turn on the tap assuming the flow is endless, without accountability for consumption.

### The Solution
**Save Our Water** transforms passive water consumption into active environmental stewardship through:
- 💧 **Real-time water usage tracking**
- 🎯 **Gamified conservation goals**
- 📊 **Impact visualization & contextual insights**
- 🏆 **Achievement system** for behavioral change
- 🌍 **Educational content** on water conservation

---

## 🚀 Features

### Core Functionality
- **Activity-based tracking** - Log showers, taps, toilet flushes, laundry, dishes
- **Automatic calculations** - Smart water usage estimates based on duration
- **Daily goals & progress** - Set conservation targets and track performance
- **Offline-first** - Works without internet, syncs when connected

### Gamification
- **Achievement system** - Unlock badges for streaks, milestones, and efficient usage
- **Streak tracking** - Build daily conservation habits
- **Progress visualization** - Beautiful charts and graphs
- **Notifications** - Celebrate achievements and daily reminders

### Value Awareness
- **Contextual insights** - "You saved enough water to sustain a family for a week!"
- **Real-world comparisons** - Translate liters into tangible equivalents
- **Educational content** - Water facts, conservation tips, best practices
- **Impact calculator** - Project savings from behavioral changes

---

## 🛠️ Technology Stack

### Android Development
- **Language:** Kotlin
- **Min SDK:** API 21 (Android 5.0 Lollipop)
- **Target SDK:** API 34 (Android 14)
- **IDE:** Android Studio (latest stable)

### Architecture
- **Pattern:** MVVM (Model-View-ViewModel)
- **Architecture Components:**
  - ViewModel - Lifecycle-aware UI state management
  - LiveData - Observable data holder
  - Room Database - Local SQLite abstraction
  - Navigation Component - Fragment navigation
  - WorkManager - Background sync scheduling

### Libraries & Dependencies
- **UI:** Material Design 3, MotionLayout
- **Charts:** MPAndroidChart
- **Networking:** Retrofit 2 + OkHttp
- **Database:** Room + Supabase (cloud sync)
- **Async:** Kotlin Coroutines + Flow
- **Image Loading:** Coil
- **Testing:** JUnit 5, Espresso, MockK

### Backend & Cloud
- **Database:** Supabase (PostgreSQL)
- **Authentication:** Supabase Auth (email/password)
- **API:** RESTful with Row Level Security
- **Hosting:** Supabase free tier

---

## 📁 Project Structure

```
SaveOurWater/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/saveourwater/
│   │   │   │   ├── ui/                    # UI layer (Fragments, Activities)
│   │   │   │   │   ├── home/
│   │   │   │   │   ├── tracking/
│   │   │   │   │   ├── achievements/
│   │   │   │   │   ├── insights/
│   │   │   │   │   └── onboarding/
│   │   │   │   ├── data/                  # Data layer
│   │   │   │   │   ├── local/             # Room Database
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   └── database/
│   │   │   │   │   ├── remote/            # Supabase API
│   │   │   │   │   │   ├── api/
│   │   │   │   │   │   └── dto/
│   │   │   │   │   └── repository/        # Repository pattern
│   │   │   │   ├── domain/                # Business logic
│   │   │   │   │   ├── model/
│   │   │   │   │   └── usecase/
│   │   │   │   └── utils/                 # Utilities
│   │   │   ├── res/                       # Resources
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   ├── navigation/
│   │   │   │   └── menu/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                          # Unit tests
│   │   └── androidTest/                   # Instrumentation tests
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🎯 Development Timeline

**Duration:** January 23 - February 13, 2026 (3 weeks)

### Phase 1: Foundation (Jan 23-25)
- Project setup, database schema, Supabase configuration

### Phase 2: Core Tracking (Jan 26-30)
- Activity logging, dashboard, calculations

### Phase 3: Gamification (Jan 31 - Feb 4)
- Achievements, streaks, notifications

### Phase 4: Value Awareness (Feb 5-7)
- Insights, education, impact calculator

### Phase 5: UI Polish (Feb 8-11)
- Material Design 3, animations, dark mode, onboarding

### Phase 6: Testing & Deployment (Feb 12-13)
- QA, APK generation, Play Store preparation

---

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (latest stable version)
- JDK 17+
- Android SDK API 21+
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/SaveOurWater.git
cd SaveOurWater
```

2. **Open in Android Studio**
- File → Open → Select project directory
- Wait for Gradle sync to complete

3. **Configure Supabase** (Optional for cloud sync)
- Create a [Supabase](https://supabase.com) account
- Create a new project
- Copy your Project URL and Anon Key
- Create `gradle.properties` in project root:
```properties
supabase.anon.key=YOUR_SUPABASE_ANON_KEY_HERE
```

4. **Build and Run**
- Select an emulator or connected device
- Click Run (▶️) or press `Shift + F10`

---

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```

---

## 📱 Building APK

### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact

**Project Team:**
- Developer: 
 * ####  😎 [Jay Arre Talosig](https://github.com/flexycode) - Machine Learning Engineer | Blockchain Developer    
 * ####  🕵️‍♀️ [Jude Renwell B. Prodigalidad](https://github.com/judeprod) - Machine Learning Engineer | Mobile App Developer | Software Engineer 
 * ####  🕵️ [Diana Grace Paray](https://github.com/) - Digital Forensics Analyst | Software Engineer | UI/UX Designer
 * ####  🥷 [Kris Brian Diaz](https://github.com/) - Digital Forensics Analyst | Software Engineer | UI/UX Designer

- Project Link: [https://github.com/judeprod/AppDev-Final-Project](https://github.com/judeprod/AppDev-Final-Project)

---

## 🙏 Acknowledgments

- **Inspiration:** The global water crisis and the need for individual accountability
- **Design:** Material Design 3 guidelines
- **Libraries:** Android Jetpack, Supabase, MPAndroidChart community
- **Icons:** Material Icons

---

**Making water visible, valuable, and sustainable - one drop at a time.** 💧🌍