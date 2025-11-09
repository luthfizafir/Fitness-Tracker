package com.example.fitnesstracker.domain

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.PI

data class RepState(
    val reps: Int,
    val stage: String?,
    val elbowDeg: Int,
    val hipDeg: Int,
    val hipOk: Boolean
)

class PoseRepCounter(
    private val elbowDownMax: Int = 90,   // <= 90° counts as bottom
    private val elbowUpMin: Int = 160,    // >= 160° counts as top
    private val hipTol: Int = 15          // |hip-180| < tol for straight
) {
    private var stage: String? = null
    private var reps: Int = 0

    fun reset() {
        stage = null
        reps = 0
    }

    fun update(elbowDeg: Int, hipDeg: Int): RepState {
        val hipOk = abs(hipDeg - 180) < hipTol

        if (elbowDeg > elbowUpMin) {
            stage = "up"
        }
        if (elbowDeg < elbowDownMax && stage == "up" && hipOk) {
            stage = "down"
            reps += 1
        }

        return RepState(
            reps = reps,
            stage = stage,
            elbowDeg = elbowDeg,
            hipDeg = hipDeg,
            hipOk = hipOk
        )
    }

    companion object {
        data class P(val x: Float, val y: Float)

        fun calculateAngleDeg(a: P, b: P, c: P): Int {
            val rad = atan2((c.y - b.y), (c.x - b.x)) - atan2((a.y - b.y), (a.x - b.x))
            var ang = kotlin.math.abs(rad * 180.0 / PI)
            if (ang > 180.0) ang = 360.0 - ang
            return ang.toInt()
        }
    }
}





