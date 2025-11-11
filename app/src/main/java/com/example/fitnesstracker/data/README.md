# Fitness Tracker Database

## Overview
The Fitness Tracker app uses Room Database to store user data, exercises, and workout sessions.

## Database Schema

### Version 2

#### Tables

1. **users**
   - `id` (Long, Primary Key, Auto-increment)
   - `name` (String, Required)
   - `email` (String, Required)
   - `passwordHash` (String, Optional) - For storing hashed passwords
   - `createdAt` (Date, Required)
   - `lastLoginAt` (Date, Optional)
   - `profileImageUri` (String, Optional)

2. **exercises**
   - `id` (Long, Primary Key, Auto-increment)
   - `name` (String, Required) - e.g., "Push-Ups", "Squats"
   - `description` (String, Optional)
   - `muscleGroups` (String, Optional) - Comma-separated list
   - `difficulty` (String, Optional) - "Beginner", "Intermediate", "Advanced"
   - `iconResId` (String, Optional)
   - `isActive` (Boolean, Default: true)

3. **workout_sessions**
   - `id` (Long, Primary Key, Auto-increment)
   - `userId` (Long, Optional, Foreign Key -> users.id)
   - `exerciseId` (Long, Optional, Foreign Key -> exercises.id)
   - `workoutType` (String, Required) - e.g., "Push-Ups"
   - `date` (Date, Required)
   - `totalReps` (Int, Required)
   - `avgTempo` (Float, Required) - seconds per rep
   - `formScore` (Float, Required) - 0-100
   - `goalReps` (Int, Required)
   - `duration` (Long, Required) - milliseconds
   - `feedbackType` (String, Required) - "Voice", "Visual", "Both"
   - `avgElbowAngle` (Float, Optional) - For push-up counter
   - `avgHipAngle` (Float, Optional) - For push-up counter
   - `goodFormPercentage` (Float, Optional) - Percentage of reps with good form
   - `notes` (String, Optional)

## Usage

### Getting Database Instance

```kotlin
val database = AppDatabase.getDatabase(context)
val userDao = database.userDao()
val exerciseDao = database.exerciseDao()
val workoutSessionDao = database.workoutSessionDao()
```

### Using Repository

```kotlin
val repository = FitnessRepository(userDao, exerciseDao, workoutSessionDao)

// Initialize default exercises
lifecycleScope.launch {
    repository.initializeDefaultExercises()
}

// Create user
lifecycleScope.launch {
    val user = User(
        name = "John Doe",
        email = "john@example.com",
        passwordHash = hashedPassword
    )
    val userId = repository.insertUser(user)
}

// Save workout session
lifecycleScope.launch {
    val session = WorkoutSession(
        userId = currentUserId,
        workoutType = "Push-Ups",
        date = Date(),
        totalReps = 25,
        avgTempo = 2.5f,
        formScore = 85f,
        goalReps = 20,
        duration = 60000L,
        feedbackType = "Both",
        avgElbowAngle = 75f,
        avgHipAngle = 178f,
        goodFormPercentage = 90f
    )
    repository.insertSession(session)
}

// Get user sessions
val userSessions = repository.getSessionsByUser(userId)
```

## Migration

The database includes a migration from version 1 to 2 that:
1. Creates the `users` table
2. Creates the `exercises` table
3. Adds new columns to `workout_sessions` table
4. Creates indices for better query performance
5. Inserts default exercises

## Default Exercises

The database automatically includes these default exercises:
- Push-Ups
- Squats
- Pull-Ups
- Planks

## Notes

- The database uses `fallbackToDestructiveMigration()` in development mode. Remove this in production.
- User passwords should be hashed before storing (not implemented in this example).
- Foreign keys are optional to maintain backward compatibility with existing data.



