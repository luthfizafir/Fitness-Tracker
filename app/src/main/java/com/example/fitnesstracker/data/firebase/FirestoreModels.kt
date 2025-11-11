package com.example.fitnesstracker.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FirestoreUser(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val lastLoginAt: Timestamp? = null,
    val profileImageUrl: String? = null
)

data class FirestoreExercise(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val muscleGroups: String? = null,
    val difficulty: String? = null,
    val iconResId: String? = null,
    val isActive: Boolean = true
)

data class FirestoreWorkoutSession(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val exerciseId: String? = null,
    val workoutType: String = "",
    val date: Timestamp = Timestamp.now(),
    val totalReps: Int = 0,
    val avgTempo: Float = 0f,
    val formScore: Float = 0f,
    val goalReps: Int = 0,
    val duration: Long = 0L,
    val feedbackType: String = "",
    val avgElbowAngle: Float? = null,
    val avgHipAngle: Float? = null,
    val goodFormPercentage: Float? = null,
    val notes: String? = null
)


