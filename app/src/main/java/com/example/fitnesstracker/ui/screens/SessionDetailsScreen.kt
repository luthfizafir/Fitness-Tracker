package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.fitnesstracker.data.SessionItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(navController: NavController, sessionId: Long) {
    // This should fetch from database
    val session = SessionItem(sessionId, "Push-Ups", Date(), 15, 87f)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Workout Type", style = MaterialTheme.typography.titleMedium)
                    Text(session.workoutType, style = MaterialTheme.typography.headlineSmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Date", style = MaterialTheme.typography.titleMedium)
                    Text(
                        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                            .format(session.date),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Reps", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${session.reps} reps",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Form Score", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${session.formScore.toInt()}%",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
