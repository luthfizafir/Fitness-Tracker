package com.example.fitnesstracker.data

import java.util.Date

data class SessionItem(
    val id: Long,
    val workoutType: String,
    val date: Date,
    val reps: Int,
    val formScore: Float
)

