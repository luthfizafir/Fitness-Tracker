package com.example.fitnesstracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["exerciseId"])]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long? = null, // Link to user (optional for now)
    val exerciseId: Long? = null, // Link to exercise type
    val workoutType: String, // e.g., "Push-Ups" (kept for backward compatibility)
    val date: Date,
    val totalReps: Int,
    val avgTempo: Float, // seconds per rep
    val formScore: Float, // 0-100
    val goalReps: Int,
    val duration: Long, // in milliseconds
    val feedbackType: String, // e.g., "Voice", "Visual", "Both"
    // Additional fields for push-up counter
    val avgElbowAngle: Float? = null, // Average elbow angle during workout
    val avgHipAngle: Float? = null, // Average hip angle during workout
    val goodFormPercentage: Float? = null, // Percentage of reps with good form
    val notes: String? = null // Additional notes or comments
)


