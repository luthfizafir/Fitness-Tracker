package com.example.fitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.FitnessRepository
import com.example.fitnesstracker.data.User
import com.example.fitnesstracker.util.PasswordHasher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class AuthViewModel(
    private val repository: FitnessRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _isFirstTimeUser = MutableStateFlow(true)
    val isFirstTimeUser: StateFlow<Boolean> = _isFirstTimeUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _signupError = MutableStateFlow<String?>(null)
    val signupError: StateFlow<String?> = _signupError.asStateFlow()

    init {
        checkFirstTimeUser()
        checkLoginStatus()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        viewModelScope.launch {
            repository.initializeDefaultExercises()
        }
    }

    private fun checkFirstTimeUser() {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            _isFirstTimeUser.value = preferences[FIRST_TIME_KEY] == null
        }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            val userId = preferences[USER_ID_KEY]
            if (userId != null) {
                val user = repository.getUserById(userId)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                }
            }
        }
    }

    suspend fun createAccount(name: String, email: String, password: String): Boolean {
        return try {
            // Check if email already exists
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                _signupError.value = "Email already registered"
                return false
            }

            // Hash password
            val passwordHash = PasswordHasher.hash(password)

            // Create user
            val user = User(
                name = name,
                email = email,
                passwordHash = passwordHash,
                createdAt = Date()
            )

            val userId = repository.insertUser(user)

            // Save to DataStore
            dataStore.edit { preferences ->
                preferences[USER_ID_KEY] = userId
                preferences[USER_NAME_KEY] = name
                preferences[USER_EMAIL_KEY] = email
                preferences[FIRST_TIME_KEY] = "false"
            }

            _currentUser.value = user.copy(id = userId)
            _isLoggedIn.value = true
            _signupError.value = null
            true
        } catch (e: Exception) {
            _signupError.value = "Failed to create account: ${e.message}"
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            _loginError.value = null

            // Find user by email
            val user = repository.getUserByEmail(email)
            if (user == null) {
                _loginError.value = "Invalid email or password"
                return false
            }

            // Verify password
            if (user.passwordHash == null || !PasswordHasher.verify(password, user.passwordHash)) {
                _loginError.value = "Invalid email or password"
                return false
            }

            // Update last login
            repository.updateLastLogin(user.id, Date())

            // Save to DataStore
            dataStore.edit { preferences ->
                preferences[USER_ID_KEY] = user.id
                preferences[USER_NAME_KEY] = user.name
                preferences[USER_EMAIL_KEY] = user.email
            }

            _currentUser.value = user
            _isLoggedIn.value = true
            _loginError.value = null
            true
        } catch (e: Exception) {
            _loginError.value = "Login failed: ${e.message}"
            false
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences.remove(USER_ID_KEY)
                preferences.remove(USER_NAME_KEY)
                preferences.remove(USER_EMAIL_KEY)
            }
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }

    fun setFirstTimeComplete() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[FIRST_TIME_KEY] = "false"
            }
        }
    }

    fun clearErrors() {
        _loginError.value = null
        _signupError.value = null
    }

    companion object {
        val FIRST_TIME_KEY = stringPreferencesKey("first_time_user")
        val USER_ID_KEY = longPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }
}


