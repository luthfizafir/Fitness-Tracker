package com.example.fitnesstracker.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    
    // Collections
    private val usersCollection = db.collection("users")
    private val exercisesCollection = db.collection("exercises")
    private val workoutSessionsCollection = db.collection("workout_sessions")

    // User operations
    suspend fun createUser(user: FirestoreUser): Result<String> {
        return try {
            val docRef = usersCollection.document(user.id)
            docRef.set(user).await()
            Result.success(user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<FirestoreUser?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(FirestoreUser::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLastLogin(userId: String): Result<Unit> {
        return updateUser(userId, mapOf("lastLoginAt" to Timestamp.now()))
    }

    // Exercise operations
    suspend fun createExercise(exercise: FirestoreExercise): Result<String> {
        return try {
            val docRef = exercisesCollection.add(exercise).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllExercises(): Flow<List<FirestoreExercise>> = callbackFlow {
        val listener = exercisesCollection
            .whereEqualTo("isActive", true)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val exercises = snapshot?.documents?.mapNotNull { 
                    it.toObject(FirestoreExercise::class.java)
                } ?: emptyList()
                
                trySend(exercises)
            }
        
        awaitClose { listener.remove() }
    }

    suspend fun getExerciseByName(name: String): Result<FirestoreExercise?> {
        return try {
            val snapshot = exercisesCollection
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()
            
            val exercise = snapshot.documents.firstOrNull()?.toObject(FirestoreExercise::class.java)
            Result.success(exercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun initializeDefaultExercises() {
        try {
            // Check if exercises already exist
            val existingExercises = exercisesCollection.limit(1).get().await()
            if (existingExercises.isEmpty) {
                val defaultExercises = listOf(
                    FirestoreExercise(
                        name = "Push-Ups",
                        description = "Upper body strength exercise targeting chest, triceps, and shoulders",
                        muscleGroups = "Chest, Triceps, Shoulders",
                        difficulty = "Beginner",
                        isActive = true
                    ),
                    FirestoreExercise(
                        name = "Squats",
                        description = "Lower body strength exercise targeting quadriceps, glutes, and hamstrings",
                        muscleGroups = "Quadriceps, Glutes, Hamstrings",
                        difficulty = "Beginner",
                        isActive = true
                    ),
                    FirestoreExercise(
                        name = "Pull-Ups",
                        description = "Upper body pulling exercise targeting back and biceps",
                        muscleGroups = "Back, Biceps",
                        difficulty = "Intermediate",
                        isActive = true
                    ),
                    FirestoreExercise(
                        name = "Planks",
                        description = "Core strength exercise targeting core and shoulders",
                        muscleGroups = "Core, Shoulders",
                        difficulty = "Beginner",
                        isActive = true
                    )
                )
                
                defaultExercises.forEach { exercise ->
                    exercisesCollection.add(exercise).await()
                }
            }
        } catch (e: Exception) {
            // Ignore errors during initialization
        }
    }

    // Workout Session operations
    suspend fun createWorkoutSession(session: FirestoreWorkoutSession): Result<String> {
        return try {
            val docRef = workoutSessionsCollection.add(session).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getWorkoutSessionsByUser(userId: String): Flow<List<FirestoreWorkoutSession>> = callbackFlow {
        val listener = workoutSessionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val sessions = snapshot?.documents?.mapNotNull { 
                    it.toObject(FirestoreWorkoutSession::class.java)
                } ?: emptyList()
                
                trySend(sessions)
            }
        
        awaitClose { listener.remove() }
    }

    suspend fun getWorkoutSession(sessionId: String): Result<FirestoreWorkoutSession?> {
        return try {
            val snapshot = workoutSessionsCollection.document(sessionId).get().await()
            val session = snapshot.toObject(FirestoreWorkoutSession::class.java)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteWorkoutSession(sessionId: String): Result<Unit> {
        return try {
            workoutSessionsCollection.document(sessionId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTotalSessionsByUser(userId: String): Result<Int> {
        return try {
            val snapshot = workoutSessionsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTotalRepsByUser(userId: String): Result<Int> {
        return try {
            val snapshot = workoutSessionsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val totalReps = snapshot.documents.sumOf { 
                it.getLong("totalReps")?.toInt() ?: 0 
            }
            Result.success(totalReps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAverageFormScoreByUser(userId: String): Result<Float> {
        return try {
            val snapshot = workoutSessionsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val scores = snapshot.documents.mapNotNull { 
                it.getDouble("formScore")?.toFloat() 
            }
            
            val average = if (scores.isNotEmpty()) {
                scores.average().toFloat()
            } else {
                0f
            }
            
            Result.success(average)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


