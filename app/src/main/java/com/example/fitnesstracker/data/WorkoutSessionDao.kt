package com.example.fitnesstracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getSessionsByUser(userId: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE exerciseId = :exerciseId ORDER BY date DESC")
    fun getSessionsByExercise(exerciseId: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE workoutType = :workoutType ORDER BY date DESC")
    fun getSessionsByWorkoutType(workoutType: String): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<WorkoutSession>)

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("SELECT COUNT(*) FROM workout_sessions")
    suspend fun getTotalSessions(): Int

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :userId")
    suspend fun getTotalSessionsByUser(userId: Long): Int

    @Query("SELECT SUM(totalReps) FROM workout_sessions")
    suspend fun getTotalReps(): Int?

    @Query("SELECT SUM(totalReps) FROM workout_sessions WHERE userId = :userId")
    suspend fun getTotalRepsByUser(userId: Long): Int?

    @Query("SELECT AVG(formScore) FROM workout_sessions WHERE userId = :userId")
    suspend fun getAverageFormScoreByUser(userId: Long): Float?

    @Query("SELECT * FROM workout_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSessionsByDateRange(startDate: java.util.Date, endDate: java.util.Date): Flow<List<WorkoutSession>>
}

