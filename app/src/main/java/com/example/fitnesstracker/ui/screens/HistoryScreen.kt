package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnesstracker.data.SessionItem
import com.example.fitnesstracker.navigation.NavRoutes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    // This should come from ViewModel/Database
    val sessions = listOf(
        SessionItem(1, "Push-Ups", Date(), 15, 87f),
        SessionItem(2, "Push-Ups", Date(System.currentTimeMillis() - 86400000), 12, 82f),
        SessionItem(3, "Push-Ups", Date(System.currentTimeMillis() - 172800000), 10, 85f)
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            sessions.forEach { session ->
                Card(
                    onClick = {
                        navController.navigate("${NavRoutes.SessionDetails.route}/${session.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = session.workoutType,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(session.date),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            Text(
                                text = "${session.reps} reps",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${session.formScore.toInt()}% form",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
