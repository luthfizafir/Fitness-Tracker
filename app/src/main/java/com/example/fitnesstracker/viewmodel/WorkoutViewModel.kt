package com.example.fitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.WorkoutSession
import com.example.fitnesstracker.data.WorkoutSessionDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class WorkoutViewModel(private val dao: WorkoutSessionDao) : ViewModel() {
    private val _selectedWorkoutType = MutableStateFlow<String?>(null)
    val selectedWorkoutType: StateFlow<String?> = _selectedWorkoutType.asStateFlow()

    private val _goalReps = MutableStateFlow(10)
    val goalReps: StateFlow<Int> = _goalReps.asStateFlow()

    private val _feedbackType = MutableStateFlow("Both")
    val feedbackType: StateFlow<String> = _feedbackType.asStateFlow()

    private val _tempo = MutableStateFlow(2.0f)
    val tempo: StateFlow<Float> = _tempo.asStateFlow()

    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()

    private val _repCount = MutableStateFlow(0)
    val repCount: StateFlow<Int> = _repCount.asStateFlow()

    private val _formScore = MutableStateFlow(85f)
    val formScore: StateFlow<Float> = _formScore.asStateFlow()

    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _workoutStartTime = MutableStateFlow<Long?>(null)
    val workoutStartTime: StateFlow<Long?> = _workoutStartTime.asStateFlow()

    fun selectWorkoutType(workoutType: String) {
        _selectedWorkoutType.value = workoutType
    }

    fun setGoalReps(reps: Int) {
        _goalReps.value = reps
    }

    fun setFeedbackType(type: String) {
        _feedbackType.value = type
    }

    fun setTempo(tempo: Float) {
        _tempo.value = tempo
    }

    fun startWorkout() {
        _isWorkoutActive.value = true
        _workoutStartTime.value = System.currentTimeMillis()
        _repCount.value = 0
    }

    fun incrementRep() {
        _repCount.value = _repCount.value + 1
    }

    fun updateFormScore(score: Float) {
        _formScore.value = score
    }

    fun stopWorkout() {
        _isWorkoutActive.value = false
        val startTime = _workoutStartTime.value ?: System.currentTimeMillis()
        val duration = System.currentTimeMillis() - startTime
        val reps = _repCount.value
        
        val session = WorkoutSession(
            workoutType = _selectedWorkoutType.value ?: "Push-Ups",
            date = Date(),
            totalReps = reps,
            avgTempo = if (reps > 0) duration.toFloat() / (reps * 1000) else 0f,
            formScore = _formScore.value,
            goalReps = _goalReps.value,
            duration = duration,
            feedbackType = _feedbackType.value
        )
        
        _currentSession.value = session
        
        viewModelScope.launch {
            dao.insertSession(session)
        }
    }

    fun resetWorkout() {
        _selectedWorkoutType.value = null
        _goalReps.value = 10
        _feedbackType.value = "Both"
        _tempo.value = 2.0f
        _repCount.value = 0
        _formScore.value = 85f
        _isWorkoutActive.value = false
        _workoutStartTime.value = null
        _currentSession.value = null
    }
}


