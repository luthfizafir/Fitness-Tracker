package com.example.fitnesstracker.ui.screens

import android.view.ViewGroup
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.fitnesstracker.navigation.NavRoutes

@Composable
fun WorkoutSessionScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    
    var repCount by remember { mutableStateOf(0) }
    var formScore by remember { mutableStateOf(85f) }
    var showStopDialog by remember { mutableStateOf(false) }
    var goalReached by remember { mutableStateOf(false) }
    val goalReps = 10 // This should come from ViewModel

    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    // Simulate rep counting
    LaunchedEffect(Unit) {
        while (!goalReached) {
            kotlinx.coroutines.delay(3000)
            repCount++
            if (repCount >= goalReps) {
                goalReached = true
                navController.navigate(NavRoutes.SessionSummary.route)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    controller = cameraController
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Reps", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "$repCount / $goalReps",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Form Score", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${formScore.toInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // Form Tips & Alerts
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Form Tips", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Keep your back straight. Lower your body until elbows are at 90 degrees.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Stop Button
            FloatingActionButton(
                onClick = { showStopDialog = true },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(Icons.Default.Close, "Stop Workout")
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Stop Workout?") },
            text = { Text("Are you sure you want to stop the workout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showStopDialog = false
                        navController.navigate(NavRoutes.SessionSummary.route)
                    }
                ) {
                    Text("Yes, Stop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
