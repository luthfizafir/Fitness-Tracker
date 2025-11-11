package com.example.fitnesstracker.util

import java.security.MessageDigest

object PasswordHasher {
    /**
     * Simple password hashing using SHA-256
     * Note: For production, use bcrypt or Argon2 instead
     */
    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verify if a password matches the hash
     */
    fun verify(password: String, hash: String): Boolean {
        return hash(password) == hash
    }
}



