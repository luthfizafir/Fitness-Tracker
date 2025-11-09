package com.example.fitnesstracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String? = null,
    val createdAt: Date,
    val lastLoginAt: Date? = null,
    val profileImageUri: String? = null
)


