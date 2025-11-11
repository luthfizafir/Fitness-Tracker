package com.example.fitnesstracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g., "Push-Ups", "Squats", "Pull-Ups"
    val description: String? = null,
    val muscleGroups: String? = null, // Comma-separated: "Chest, Triceps, Shoulders"
    val difficulty: String? = null, // "Beginner", "Intermediate", "Advanced"
    val iconResId: String? = null, // Resource identifier or icon name
    val isActive: Boolean = true
)



