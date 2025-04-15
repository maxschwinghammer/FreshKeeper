<div align="center">
    <a href="https://play.google.com/store/apps/details?id=com.freshkeeper">
      <img src="https://github.com/user-attachments/assets/04b829bc-c29d-4739-9c5d-530c13aecd26" width="150" height="150" alt="logo-transparent" />
    </a>
</div>
<div align="center">
    <a href="https://play.google.com/store/apps/details?id=com.freshkeeper">
      <img src="https://github.com/user-attachments/assets/a643742b-7e31-46ef-90f5-857bb57ceab1" width="250 "alt="logo-transparent" />
    </a>
</div>

---

![GitHub Stars](https://img.shields.io/github/stars/maxschwinghammer/FreshKeeper?style=social)
![GitHub Watchers](https://img.shields.io/github/watchers/maxschwinghammer/FreshKeeper?style=social)
![GitHub Contributors](https://img.shields.io/github/contributors/maxschwinghammer/FreshKeeper)
![GitHub Last Commit](https://img.shields.io/github/last-commit/maxschwinghammer/FreshKeeper)
![GitHub Issues](https://img.shields.io/github/issues/maxschwinghammer/FreshKeeper)
[![Android CI/CD](https://github.com/maxschwinghammer/FreshKeeper/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/maxschwinghammer/FreshKeeper/actions/workflows/android.yml)
[![CodeQL](https://github.com/maxschwinghammer/FreshKeeper/actions/workflows/codeql.yml/badge.svg?branch=master)](https://github.com/maxschwinghammer/FreshKeeper/actions/workflows/codeql.yml)

---

## About
FreshKeeper is a mobile application designed to help households effectively manage their food inventories and reduce waste through smart tracking of expiration dates. The app streamlines food management by integrating a barcode scanner, timely notifications, and organized inventory management. It provides users with real-time product information by integrating public APIs for enhanced food data, and features a modern, minimalist UI powered by Jetpack Compose.

### Features
- **Food Inventory Management**: Track expiration dates and organize food items by location (e.g., fridge, pantry)
- **Timely Notifications**: Receive alerts before food items expire
- **Barcode Scanner Integration**: Quickly add products by scanning their barcode
- **API-Driven Enhancements**: Retrieve detailed product information from public APIs
- **Modern UI/UX**: Built with Kotlin and Jetpack Compose following the MVVM pattern for clean architecture
- **Secure Multi-User Support**: User authentication via OAuth 2.0 with Firebase Authentication and robust data management using Firebase Firestore
- **Continuous Integration & Delivery**: Automated testing and builds using GitHub Actions

## Installation
You can download the app via Google Play:
<p align="left">
  <a href="https://play.google.com/store/apps/details?id=com.freshkeeper">
    <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Google_Play_Store_badge_EN.svg/2560px-Google_Play_Store_badge_EN.svg.png" width="200" alt="Get it on Google Play" />
  </a>
</p>

To install and run FreshKeeper locally, ensure you have [Android Studio](https://developer.android.com/studio) installed. 

1. Clone the repository:
```
git clone https://github.com/maxschwinghammer/FreshKeeper.git
```
2. Sync the project with Gradle files
3. Build and run the app by executing MainActivity.kt on either an Android emulator or a physical device
