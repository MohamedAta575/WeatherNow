# ☀️ WeatherNow

**Your Personal Weather Companion: Reliable, Instant Forecasts Built with Modern Android Tech — now with smart notifications!**

[![GitHub Repo stars](https://img.shields.io/github/stars/MohamedAta575/WeatherNow?style=social)](https://github.com/MohamedAta575/WeatherNow/stargazers)
[![GitHub license](https://img.shields.io/github/license/MohamedAta575/WeatherNow)](https://github.com/MohamedAta575/WeatherNow/blob/main/LICENSE)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blueviolet)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-ff69b4)
![Android](https://img.shields.io/badge/Platform-Android-green)

---

**Weather Now** is a modern Android app that lets you check the weather anywhere—either by searching for a location or using your current location via the **"Use Current Location"** button.  
It also supports **real-time smart notifications** that automatically update with the latest weather information based on your time and location.

Stay updated with real-time weather data and forecasts for the next seven days.

---

## 📸 Screenshots

| Search & Popular Cities | Weather Details & Forecast |
| :----------------------: | :-----------------------: |
| ![Screenshot 1](images/screenshot1.png) | ![Screenshot 2](images/screenshot2.png) |
| ![Screenshot 3](images/screenshot3.png) | ![Screenshot 4](images/screenshot4.png) |
| ![Screenshot 5](images/screenshot5.png) | ![Screenshot 6](images/screenshot6.png) |

---

## 🎥 Video Demo

Watch a quick demonstration of the app's user interface and core features:

[![WeatherNow Demo Thumbnail](https://img.youtube.com/vi/dG0z4xN7_vs/0.jpg)](https://www.youtube.com/shorts/dG0z4xN7_vs)

---

## 🚀 Features

- 🔍 **Search weather** for any location.
- 📍 **Use current location** for instant local weather.
- 📅 View daily and **7-day weather forecasts**.
- 🛎️ **Smart notifications** that automatically update with weather changes and time of day.
- 💾 Persistent user login and data storage using **DataStore**.
- 🛡 **Clean Architecture and MVI pattern** for maintainable code.
- 🔧 **Dependency Injection** with **Hilt** for scalable development.

---

## 🛎️ Notifications

WeatherNow provides **dynamic weather notifications** to keep you updated without even opening the app:
- 🌤 **Morning notification**: Start your day with the current temperature and condition.
- 🌙 **Evening summary**: Get a brief forecast for the next day.
- ⚡ **Instant updates**: Receive alerts when the weather changes significantly (e.g., rain, temperature drop).
- 🔄 Notifications **refresh automatically** in the background using Android’s WorkManager and system alarms.

Notifications are designed to be **lightweight, non-intrusive, and adaptive** to your location and time.

---

## 🛠 Tech Stack

| Category | Technologies |
| :--- | :--- |
| **Language & UI** | **Android** (Kotlin), **Jetpack Compose** |
| **Architecture** | **Clean Architecture & MVI Pattern** |
| **Networking** | **Retrofit** (Weather API integration) |
| **Backend & Storage** | **Firebase** (Authentication), **DataStore** (User Preferences) |
| **Dependency Injection** | **Hilt** |
| **Background Tasks** | **WorkManager** for notifications and updates |

---

## ⚙️ Requirements

- **Android Studio** (latest version)
- Android device or emulator
- **Weather API key** (must be added in `local.properties`)
- **Firebase Project** configured for Android.

---

## ▶️ How to Run

1. **Clone the repository:**
    ```bash
    git clone https://github.com/MohamedAta575/WeatherNow.git
    ```

2. **Configure API Key:**
    * Create a file named **`local.properties`** in the root directory.
    * Add your API key:
        ```properties
        WEATHER_API_KEY="YOUR_API_KEY_HERE"
        ```

3. **Setup Firebase:**
    * Create and configure a **Firebase Project** for your Android application.
    * Download the **`google-services.json`** file and place it in the **`app/`** directory.

4. **Run the App:**
    * Open the project in Android Studio, sync Gradle, and run on a device/emulator.

---

## 📜 License

Distributed under the **MIT License**.  
See the [LICENSE](https://github.com/MohamedAta575/WeatherNow/blob/main/LICENSE) file for more information.
