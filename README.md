# 🧘 YinYoga Admin

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/Language-Java%20%2F%20Android-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Android development" />
</p>

<p align="center">
  <strong>Administration app for the YinYoga mobile application.</strong>
  <br />
  A centralized interface for managing yoga-related application data and administrative workflows.
</p>

---

## 📖 Overview

**YinYoga Admin** is an Android application developed as part of the YinYoga project. It provides an administrative interface designed to support application management through a mobile-friendly experience.

The project integrates Firebase services for data-related functionality and Google authentication services, alongside Android UI components.

## ✨ Features

- **Administrative Interface** — A dedicated interface for administrative workflows.
- **Firebase Integration** — Uses Firebase Firestore for cloud-based data storage.
- **Google Authentication Integration** — Includes Google Play Services authentication dependencies.
- **Material Design Components** — Uses AndroidX and Material Components to build the interface.
- **Reusable UI Components** — Uses RecyclerView and ConstraintLayout for Android layouts.

> Feature descriptions should be aligned with the actual implementation as the project evolves.

## 📱 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/9c0855ec-3b11-4c96-9508-ca7e355f1a35" width="22%" alt="YinYoga Admin screen 1" />
  &nbsp;
  <img src="https://github.com/user-attachments/assets/58cec233-2b95-4aec-80de-ce51907cb3d3" width="22%" alt="YinYoga Admin screen 2" />
  &nbsp;
  <img src="https://github.com/user-attachments/assets/ca54db7c-1bc7-4e7b-8060-1155ecffa39e" width="22%" alt="YinYoga Admin screen 3" />
  &nbsp;
  <img src="https://github.com/user-attachments/assets/77ea1d18-ebc1-43d3-8157-f20b703e34c5" width="22%" alt="YinYoga Admin screen 4" />
</p>

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Android SDK | Native Android application development |
| Gradle Kotlin DSL | Build configuration and dependency management |
| AndroidX AppCompat | Backward-compatible Android UI components |
| Material Components | UI components and design |
| ConstraintLayout | Flexible screen layouts |
| RecyclerView | Efficient list rendering |
| Firebase Firestore | Cloud database integration |
| Google Play Services Auth | Authentication integration |
| JUnit | Unit testing |
| Espresso | Android UI testing |

## 🏗️ Project Structure

```text
YinYoga-admin/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Application source code
│   │   │   ├── res/           # Layouts, drawables, and resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/              # Unit tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml     # Centralized dependency versions
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── README.md
```

## 🚀 Getting Started

### Prerequisites

Before running the project, make sure you have:

- [Android Studio](https://developer.android.com/studio)
- Android SDK 34
- JDK 8-compatible Java source and target configuration
- Git

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/MobileApp-YinYoga/YinYoga-admin.git
cd YinYoga-admin
```

**2. Open the project**

Open the project directory in Android Studio and allow Gradle to sync dependencies.

**3. Configure Firebase**

If Firebase services are required, configure the project with the appropriate Firebase project settings and place the `google-services.json` file in the `app/` directory.

Do not commit private credentials or sensitive configuration files to the repository.

**4. Run the application**

Select an Android emulator or connected device in Android Studio, then click **Run**.

Alternatively, build the debug APK using the Gradle wrapper.

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

## 🧪 Testing

Run unit tests:

```bash
./gradlew test
```

Run Android instrumentation tests on a configured device or emulator:

```bash
./gradlew connectedAndroidTest
```

On Windows, use `.\gradlew.bat` instead of `./gradlew`.

## 🔐 Configuration & Security

- Keep Firebase configuration and sensitive credentials out of public commits when applicable.
- Configure authentication providers in the Firebase project before using related functionality.
- Validate access permissions for administrative operations.
- Never store passwords, API secrets, or private keys directly in source code.

## 👥 Contributors

Developed as part of the **YinYoga** project.

- **Repository:** [YinYoga-admin](https://github.com/MobileApp-YinYoga/YinYoga-admin)
- **Organization:** [MobileApp-YinYoga](https://github.com/MobileApp-YinYoga)

---

<p align="center">
  Made with ❤️ for the YinYoga project.
</p>
