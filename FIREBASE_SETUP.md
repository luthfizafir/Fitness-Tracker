# Firebase Setup Instructions

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or select an existing project
3. Enter project name: `FitnessTracker` (or your preferred name)
4. Click **Continue**
5. (Optional) Enable Google Analytics
6. Click **Create project**
7. Wait for project to be created, then click **Continue**

## Step 2: Add Android App to Firebase

1. In Firebase Console, click the **Android icon** to add an Android app
2. Register app with these details:
   - **Android package name**: `com.example.fitnesstracker`
   - **App nickname** (optional): Fitness Tracker
   - **Debug signing certificate SHA-1** (optional for now)
3. Click **Register app**

## Step 3: Download Configuration File

1. Click **Download google-services.json**
2. **IMPORTANT**: Place the downloaded `google-services.json` file in your `app/` folder
   ```
   FitnessTracker/
   ├── app/
   │   ├── google-services.json  ← Place it here
   │   ├── build.gradle.kts
   │   └── src/
   ├── build.gradle.kts
   └── settings.gradle.kts
   ```
3. Click **Next**
4. Click **Continue to console** (dependencies are already added in code)

## Step 4: Enable Authentication

1. In Firebase Console, go to **Build** → **Authentication**
2. Click **Get started**
3. Click on **Email/Password** under Sign-in providers
4. **Enable** the toggle for Email/Password
5. Click **Save**

## Step 5: Enable Firestore Database

1. In Firebase Console, go to **Build** → **Firestore Database**
2. Click **Create database**
3. Select **Start in test mode** (for development)
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.time < timestamp.date(2025, 2, 9);
       }
     }
   }
   ```
4. Choose a Firestore location (select closest to your users)
5. Click **Enable**

## Step 6: Update Firestore Security Rules (Important!)

After testing, update your Firestore rules for production:

1. Go to **Firestore Database** → **Rules**
2. Replace with these secure rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own user document
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Anyone authenticated can read exercises
    match /exercises/{exerciseId} {
      allow read: if request.auth != null;
      allow write: if false; // Only admins can write (set via Firebase Console)
    }
    
    // Users can only read/write their own workout sessions
    match /workout_sessions/{sessionId} {
      allow read, write: if request.auth != null && 
                            resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && 
                       request.resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Click **Publish**

## Step 7: Sync and Build

1. **Sync Gradle** in Android Studio (click the elephant icon or File → Sync Project with Gradle Files)
2. **Clean and Rebuild** the project:
   - Build → Clean Project
   - Build → Rebuild Project
3. **Run the app**

## Step 8: Test the App

1. Open the app
2. Click **Get Started** → **Create Account**
3. Fill in:
   - Name
   - Email
   - Password (at least 6 characters)
4. Click **Create Account**
5. Check Firebase Console:
   - **Authentication** → **Users** (you should see your new user)
   - **Firestore Database** → **Data** (you should see `users` collection)

## Troubleshooting

### Error: "google-services.json not found"
- Make sure `google-services.json` is in the `app/` folder (same level as `app/build.gradle.kts`)
- Sync Gradle again

### Error: "Default FirebaseApp is not initialized"
- Make sure you've added `google-services.json`
- Check that `apply(plugin = "com.google.gms.google-services")` is in `app/build.gradle.kts`
- Clean and rebuild the project

### Error: "PERMISSION_DENIED" in Firestore
- Check your Firestore Security Rules
- Make sure you're authenticated (logged in)
- Verify the rules allow your operation

### Error: "Network error"
- Check your internet connection
- Make sure Firebase services are enabled in Firebase Console

## What's Stored in Firebase

### Authentication
- User email and password (encrypted by Firebase)
- User UID (unique identifier)
- Display name

### Firestore Collections

1. **users** collection:
   - User profile data (name, email, creation date, last login)

2. **exercises** collection:
   - Exercise types (Push-Ups, Squats, Pull-Ups, Planks)
   - Exercise details (description, muscle groups, difficulty)

3. **workout_sessions** collection:
   - Workout history for each user
   - Reps, form scores, angles, duration, etc.

## Data Migration

All new data will be saved to Firebase. The old Room database data is not automatically migrated. If you need to keep old data:

1. Export data from Room database before switching
2. Manually import into Firebase via Firebase Console or write a migration script

## Next Steps

- Set up Firebase Analytics (optional)
- Add Firebase Crashlytics for crash reporting (optional)
- Set up Firebase Cloud Messaging for notifications (optional)
- Configure production security rules before launching

## Support

If you encounter issues:
1. Check [Firebase Documentation](https://firebase.google.com/docs/android/setup)
2. Check [Firebase Authentication Guide](https://firebase.google.com/docs/auth/android/start)
3. Check [Firestore Get Started Guide](https://firebase.google.com/docs/firestore/quickstart)


