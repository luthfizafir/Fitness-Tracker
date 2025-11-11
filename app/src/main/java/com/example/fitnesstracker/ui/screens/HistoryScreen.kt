package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnesstracker.data.AppDatabase
import com.example.fitnesstracker.data.WorkoutSession
import com.example.fitnesstracker.navigation.NavRoutes
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    
    // Fetch all workout sessions
    val sessionsFlow: Flow<List<WorkoutSession>> = remember {
        database.workoutSessionDao().getAllSessions()
    }
    val sessions by sessionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History & Stats") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (sessions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "No workouts",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No workouts yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Complete a workout to see your history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Button(
                        onClick = { navController.navigate(NavRoutes.SelectWorkout.route) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Start Workout")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Recent Workouts",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                }
                
                items(sessions.sortedByDescending { it.date }) { session ->
                    WorkoutSessionCard(session, navController)
                }
                
                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun WorkoutSessionCard(session: WorkoutSession, navController: NavController) {
    Card(
        onClick = {
            navController.navigate("${NavRoutes.SessionDetails.route}/${session.id}")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.workoutType,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                            .format(session.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Rep count badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${session.totalReps} reps",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Quality metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QualityMetric(
                    label = "Form Quality",
                    value = "${session.goodFormPercentage?.toInt() ?: session.formScore.toInt()}%",
                    color = when {
                        (session.goodFormPercentage ?: session.formScore) >= 80f -> 
                            androidx.compose.ui.graphics.Color(0xFF00C853)
                        (session.goodFormPercentage ?: session.formScore) >= 60f -> 
                            androidx.compose.ui.graphics.Color(0xFFFF9800)
                        else -> 
                            androidx.compose.ui.graphics.Color(0xFFE53935)
                    }
                )
                
                if (session.avgElbowAngle != null) {
                    QualityMetric(
                        label = "Avg Elbow",
                        value = "${session.avgElbowAngle?.toInt()}°",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                QualityMetric(
                    label = "Duration",
                    value = "${(session.duration / 1000).toInt()}s",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            // Notes/Good form reps
            session.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun QualityMetric(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
