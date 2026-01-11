# OkPAssignment - Android OTP Authentication

This Android application demonstrates a complete authentication flow using Firebase, featuring Email OTP verification via Firebase Cloud Functions and Google Sign-In.

## Features
- **Email OTP Sign-In**: Users receive a 4-digit code via email (powered by SendGrid & Firebase Cloud Functions).
- **Google Sign-In**: Standard Google authentication integration.
- **Glassmorphism UI**: Modern and premium aesthetic for the onboarding and authentication screens.

## Prerequisites
- **Android Studio** (Latest version recommended)
- **Java 11** or higher
- **Firebase Project**: You need a Firebase project set up with:
    - FirebaseAuth (Google & Custom Token enabled)
    - Cloud Functions (Deployed for OTP generation/verification)
    - Firestore (For storing temporary OTP codes)

## Getting Started

### 1. Firebase Setup
- Download the `google-services.json` file from your Firebase Console.
- Place it in the `app/` directory of the project.

### 2. Cloud Functions
Ensure you have the following functions deployed in the `asia-south1` region (or update the region in `AuthRepository.kt`):
- `requestOtp`: Generates and emails the OTP.
- `verifyOtp`: Verifies the OTP and returns a Firebase Custom Token.

### 3. Build & Run
1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Connect an Android device or start an emulator.
4. Click **Run 'app'**.

## Project structure
- `fragments/`: Contains the UI logic for Welcome, Email Input, Verification, and Success screens.
- `repository/`: `AuthRepository.kt` handles interaction with Firebase Auth and Cloud Functions.
- `res/navigation/`: Defines the app's navigation graph (`nav_graph.xml`).

## Troubleshooting
- **NOT_FOUND Error**: Ensure your Cloud Functions are deployed to the `asia-south1` region as specified in `AuthRepository.kt`.
- **OTP Not Received**: Check your SendGrid API key and Firebase Functions logs in the Firebase Console.
- **Verification Failed**: Verify that the OTP length is exactly 4 digits and that the email used for verification matches the one used for the request.
