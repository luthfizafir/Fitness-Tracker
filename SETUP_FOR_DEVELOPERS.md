# Developer Setup Guide

## Prerequisites
- Android Studio (latest version)
- JDK 17 or higher
- Git installed

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/luthfizafir/Fitness-Tracker.git
cd Fitness-Tracker
```

### 2. Firebase Configuration (REQUIRED)

⚠️ **The app will NOT work without this step!**

#### Option A: Use Existing Firebase Project (Team Members)
1. Contact the project owner (luthfizafir) to:
   - Be added to the Firebase project
   - Receive the `google-services.json` file
2. Place the `google-services.json` file in:
   ```
   FitnessTracker/app/google-services.json
   ```

#### Option B: Create Your Own Firebase Project (Independent Development)
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Name your project (e.g., "FitnessTracker-Dev")
4. Complete the setup wizard

5. **Add Android App:**
   - Click the Android icon
   - Package name: `com.example.fitnesstracker`
   - Download `google-services.json`
   - Place it in `app/` folder

6. **Enable Firebase Services:**
   - **Authentication:**
     - Go to Build → Authentication → Get Started
     - Enable "Email/Password" sign-in method
   
   - **Firestore Database:**
     - Go to Build → Firestore Database → Create Database
     - Start in "Test mode" for development
     - Choose your preferred location

### 3. Sync and Build

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. If you see errors, try:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

### 4. Run the App

1. Connect an Android device or start an emulator
2. Click the "Run" button (▶️) in Android Studio
3. The app should install and launch

## 🔧 Troubleshooting

### Error: "google-services.json not found"
- Make sure the file is in `app/google-services.json`
- The file should be at the same level as `app/build.gradle.kts`
- Try: File → Sync Project with Gradle Files

### Error: "Default FirebaseApp is not initialized"
- Verify `google-services.json` is in the correct location
- Check that the package name matches: `com.example.fitnesstracker`
- Clean and rebuild: Build → Clean Project → Rebuild Project

### Error: "CONFIGURATION_NOT_FOUND"
- You're missing the `google-services.json` file
- See "Firebase Configuration" section above

### Build Errors
```bash
# Clean the project
./gradlew clean

# Invalidate caches in Android Studio
File → Invalidate Caches → Invalidate and Restart
```

### Firebase Permission Errors
- Make sure you've enabled Authentication and Firestore in Firebase Console
- Check that Firestore security rules allow read/write in test mode

## 📱 Testing the App

### First Time Setup
1. Launch the app
2. Click "Get Started"
3. Create an account with:
   - Name: Test User
   - Email: test@example.com
   - Password: test123 (minimum 6 characters)
4. Complete the intro screens
5. Grant camera permission (required for workout tracking)

### Features to Test
- ✅ Sign up / Login
- ✅ Logout (Settings → Logout)
- ✅ Push-up counter (Home → Start Workout → Push-Ups)
- ✅ Camera permission handling
- ✅ Navigation flow

## 🔐 Security Notes

- **NEVER commit `google-services.json` to GitHub**
- It's already in `.gitignore` for security
- Share the file privately with team members only
- Each developer can use their own Firebase project for testing

## 📚 Additional Resources

- [Firebase Setup Guide](FIREBASE_SETUP.md) - Detailed Firebase configuration
- [Android Documentation](https://developer.android.com/)
- [Firebase Documentation](https://firebase.google.com/docs/android/setup)
- [ML Kit Pose Detection](https://developers.google.com/ml-kit/vision/pose-detection)

## 🆘 Need Help?

If you're still having issues:
1. Check the error messages in Android Studio's Logcat
2. Verify all prerequisites are installed
3. Make sure `google-services.json` is configured correctly
4. Contact the project maintainer

## 🎯 Project Structure

```
FitnessTracker/
├── app/
│   ├── google-services.json          ← PUT FIREBASE CONFIG HERE
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── java/com/example/fitnesstracker/
│           │   ├── MainActivity.kt
│           │   ├── data/              (Database & Firebase)
│           │   ├── ui/screens/        (All app screens)
│           │   ├── viewmodel/         (Business logic)
│           │   └── navigation/        (Navigation setup)
│           └── res/                   (Resources)
├── scripts/                           (Python push-up counter)
├── FIREBASE_SETUP.md                  (Firebase guide)
└── README.md
```

## 🚀 Ready to Contribute?

1. Create a new branch: `git checkout -b feature/your-feature`
2. Make your changes
3. Test thoroughly
4. Commit: `git commit -m "Add your feature"`
5. Push: `git push origin feature/your-feature`
6. Create a Pull Request on GitHub

Happy coding! 🎉

