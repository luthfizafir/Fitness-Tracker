package com.example.fitnesstracker.domain

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.PI

enum class RepQuality {
    EXCELLENT,   // Full ROM (≤90°) + straight back (±15°)
    GOOD,        // Good ROM (≤100°) + decent form (±30°)
    ACCEPTABLE   // Counted but needs work
}

data class RepState(
    val reps: Int,
    val goodFormReps: Int,
    val stage: String?,
    val elbowDeg: Int,
    val hipDeg: Int,
    val formFeedback: String,
    val repQuality: RepQuality
)

class PoseRepCounter(
    // More lenient elbow threshold (from research code)
    private val elbowDownMax: Int = 110,      // Counts as valid rep (was 90°)
    private val elbowPerfect: Int = 90,       // Excellent form threshold
    private val elbowUpMin: Int = 160,        // Full extension
    
    // Hip checking: warn but don't block by default
    private val enforceHipForm: Boolean = false,  // Don't block reps for hip form
    private val hipWarnThreshold: Int = 30,       // Warn if hip >30° off
    private val hipPerfectThreshold: Int = 15,    // Excellent form threshold
    
    // Timing protection to prevent double-counting
    private val minRepTimeMs: Long = 500          // 0.5 seconds between reps
) {
    private var stage: String? = null
    private var reps: Int = 0
    private var goodReps: Int = 0
    private var lastRepTime: Long = 0
    
    // Angle history for robust smoothing (median filtering)
    private val angleHistory = ArrayDeque<Int>(5)
    
    fun reset() {
        stage = null
        reps = 0
        goodReps = 0
        angleHistory.clear()
        lastRepTime = 0
    }

    fun update(elbowDeg: Int, hipDeg: Int): RepState {
        // Apply robust smoothing to elbow angle
        val smoothedElbow = smoothAngleRobust(elbowDeg)
        
        // Analyze hip form (don't block, just assess)
        val hipDiff = abs(hipDeg - 180)
        val hipOk = hipDiff < hipWarnThreshold
        val hipPerfect = hipDiff < hipPerfectThreshold
        
        // Assess overall rep quality
        val repQuality = when {
            smoothedElbow <= elbowPerfect && hipPerfect -> RepQuality.EXCELLENT
            smoothedElbow <= 100 && hipOk -> RepQuality.GOOD
            else -> RepQuality.ACCEPTABLE
        }
        
        // Generate dynamic feedback based on form
        val formFeedback = when {
            hipDiff > 40 && hipDeg < 180 -> "Engage core - hips sagging"
            hipDiff > 40 && hipDeg > 180 -> "Lower your hips"
            smoothedElbow > 105 && stage == "down" -> "Try going deeper"
            smoothedElbow < elbowUpMin && stage == "up" -> "Extend arms fully"
            repQuality == RepQuality.EXCELLENT -> "Perfect form! 🔥"
            repQuality == RepQuality.GOOD -> "Good form!"
            else -> "Keep it up!"
        }
        
        // Stage transitions
        if (smoothedElbow > elbowUpMin) {
            stage = "up"
        }
        
        // Count rep logic with timing protection
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRep = currentTime - lastRepTime
        
        if (smoothedElbow < elbowDownMax && 
            stage == "up" && 
            timeSinceLastRep > minRepTimeMs) {
            
            // Hip check: only block if enforceHipForm is true (default: false)
            val shouldCount = !enforceHipForm || hipOk
            
            if (shouldCount) {
                stage = "down"
                reps += 1
                lastRepTime = currentTime
                
                // Track good form reps separately
                if (repQuality != RepQuality.ACCEPTABLE) {
                    goodReps += 1
                }
            }
        }
        
        return RepState(
            reps = reps,
            goodFormReps = goodReps,
            stage = stage,
            elbowDeg = smoothedElbow,
            hipDeg = hipDeg,
            formFeedback = formFeedback,
            repQuality = repQuality
        )
    }
    
    /**
     * Robust angle smoothing using median filtering + outlier removal
     * Inspired by research code - more resistant to ML Kit noise
     */
    private fun smoothAngleRobust(angle: Int): Int {
        angleHistory.addLast(angle)
        if (angleHistory.size > 5) {
            angleHistory.removeFirst()
        }
        
        // Need at least 3 samples for meaningful smoothing
        if (angleHistory.size < 3) return angle
        
        // Use median to find center (resistant to outliers)
        val sorted = angleHistory.sorted()
        val median = sorted[sorted.size / 2]
        
        // Remove statistical outliers (>20° from median)
        val filtered = angleHistory.filter { 
            abs(it - median) < 20
        }
        
        // Return average of filtered values
        return if (filtered.isNotEmpty()) {
            filtered.average().toInt()
        } else {
            angle
        }
    }

    companion object {
        data class P(val x: Float, val y: Float)

        fun calculateAngleDeg(a: P, b: P, c: P): Int {
            val rad = atan2((c.y - b.y), (c.x - b.x)) - atan2((a.y - b.y), (a.x - b.x))
            var ang = abs(rad * 180.0 / PI)
            if (ang > 180.0) ang = 360.0 - ang
            return ang.toInt()
        }
    }
}





