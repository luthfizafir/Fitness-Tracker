package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnesstracker.navigation.NavRoutes
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SessionSummaryScreen(
    navController: NavController,
    viewModel: com.example.fitnesstracker.viewmodel.WorkoutViewModel
) {
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    
    // Debug: Log what we're receiving
    LaunchedEffect(currentSession) {
        println("SessionSummary - Received session: $currentSession")
    }
    
    val totalReps = currentSession?.totalReps ?: 0
    val goodFormPercentage = currentSession?.goodFormPercentage ?: 0f
    val avgElbowAngle = currentSession?.avgElbowAngle ?: 0f
    val avgHipAngle = currentSession?.avgHipAngle ?: 0f
    val duration = currentSession?.duration ?: 0L
    val notes = currentSession?.notes ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Workout Complete!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Great job! Here's your summary",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        // Debug Warning if no data
        if (totalReps == 0 && currentSession == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️ No Workout Data",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Session data not loaded. Please try again.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Main Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Reps",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = totalReps.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Quality Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Form Quality",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${goodFormPercentage.toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    color = when {
                        goodFormPercentage >= 80f -> androidx.compose.ui.graphics.Color(0xFF00C853)
                        goodFormPercentage >= 60f -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                        else -> androidx.compose.ui.graphics.Color(0xFFE53935)
                    }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Technical Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Technical Details",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Avg Elbow Angle",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${avgElbowAngle.toInt()}°",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Avg Hip Angle",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${avgHipAngle.toInt()}°",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${(duration / 1000).toInt()}s",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Avg Tempo",
                            style = MaterialTheme.typography.bodySmall
                        )
                        val tempo = if (totalReps > 0) (duration / 1000f) / totalReps else 0f
                        Text(
                            text = "${String.format("%.1f", tempo)}s/rep",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Action Buttons - Full Width, Large, Easy to Tap
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary Action: Back to Home
            Button(
                onClick = {
                    viewModel.resetWorkout()
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Back to Home",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Secondary Action: View History
            OutlinedButton(
                onClick = {
                    navController.navigate(NavRoutes.History.route) {
                        popUpTo(NavRoutes.Home.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "View History",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
