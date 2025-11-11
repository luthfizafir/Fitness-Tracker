package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.fitnesstracker.data.AppDatabase
import com.example.fitnesstracker.data.WorkoutSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(navController: NavController, sessionId: Long) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()
    
    var session by remember { mutableStateOf<WorkoutSession?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Fetch session from database
    LaunchedEffect(sessionId) {
        scope.launch {
            session = database.workoutSessionDao().getSessionById(sessionId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            session == null -> {
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
                        Text(
                            "Session not found",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
            else -> {
                SessionDetailsContent(session!!, paddingValues)
            }
        }
    }
}

@Composable
private fun SessionDetailsContent(session: WorkoutSession, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = session.workoutType,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                        .format(session.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))

        // Main Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total Reps",
                    value = session.totalReps.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Goal",
                    value = session.goalReps.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Duration",
                    value = "${(session.duration / 1000).toInt()}s",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Quality Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Form Quality",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(12.dp))
                
                val goodFormPercentage = session.goodFormPercentage ?: session.formScore
                val formColor = when {
                    goodFormPercentage >= 80f -> androidx.compose.ui.graphics.Color(0xFF00C853)
                    goodFormPercentage >= 60f -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                    else -> androidx.compose.ui.graphics.Color(0xFFE53935)
                }
                
                Text(
                    text = "${goodFormPercentage.toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    color = formColor
                )
                
                Spacer(Modifier.height(8.dp))
                
                session.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Technical Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                
                Spacer(Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailItem(
                        label = "Avg Elbow Angle",
                        value = "${session.avgElbowAngle?.toInt() ?: 0}°"
                    )
                    DetailItem(
                        label = "Avg Hip Angle",
                        value = "${session.avgHipAngle?.toInt() ?: 0}°"
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailItem(
                        label = "Avg Tempo",
                        value = if (session.totalReps > 0) 
                            "${String.format("%.1f", session.duration / 1000f / session.totalReps)}s/rep"
                        else "0s/rep"
                    )
                    DetailItem(
                        label = "Feedback Type",
                        value = session.feedbackType
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
