package com.example.fitnesstracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.fitnesstracker.data.firebase.FirebaseAuthService
import com.example.fitnesstracker.data.firebase.FirestoreRepository
import com.example.fitnesstracker.ui.screens.*
import com.example.fitnesstracker.viewmodel.FirebaseAuthViewModel
import com.example.fitnesstracker.viewmodel.FirebaseViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.Welcome.route,
    authService: FirebaseAuthService,
    firestoreRepository: FirestoreRepository,
    dataStore: DataStore<Preferences>
) {
    val viewModelFactory = FirebaseViewModelFactory(authService, firestoreRepository, dataStore)
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(NavRoutes.Login.route) {
            val authViewModel: FirebaseAuthViewModel = viewModel(
                factory = viewModelFactory
            )
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.CreateAccount.route) {
            val authViewModel: FirebaseAuthViewModel = viewModel(
                factory = viewModelFactory
            )
            CreateAccountScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.AppIntroduction.route) {
            AppIntroductionScreen(navController = navController)
        }
        composable(NavRoutes.PermissionsRequest.route) {
            PermissionsRequestScreen(navController = navController)
        }
        composable(NavRoutes.Tutorial.route) {
            TutorialScreen(navController = navController)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(NavRoutes.SelectWorkout.route) {
            SelectWorkoutScreen(navController = navController)
        }
        composable(NavRoutes.CustomizeSession.route) {
            CustomizeSessionScreen(navController = navController)
        }
        composable(NavRoutes.Countdown.route) {
            CountdownScreen(navController = navController)
        }
        composable(NavRoutes.WorkoutSession.route) {
            WorkoutSessionScreen(navController = navController)
        }
        composable(NavRoutes.SessionSummary.route) {
            SessionSummaryScreen(navController = navController)
        }
        composable(NavRoutes.History.route) {
            HistoryScreen(
                navController = navController,
                viewModelFactory = viewModelFactory
            )
        }
        composable("${NavRoutes.SessionDetails.route}/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
            SessionDetailsScreen(
                navController = navController,
                sessionId = sessionId ?: 0L
            )
        }
        composable(NavRoutes.Settings.route) {
            val authViewModel: FirebaseAuthViewModel = viewModel(
                factory = viewModelFactory
            )
            SettingsScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.Profile.route) {
            val authViewModel: FirebaseAuthViewModel = viewModel(
                factory = viewModelFactory
            )
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                viewModelFactory = viewModelFactory
            )
        }
        composable(NavRoutes.AppPreferences.route) {
            AppPreferencesScreen(navController = navController)
        }
        composable(NavRoutes.TutorialHelp.route) {
            TutorialHelpScreen(navController = navController)
        }
        composable(NavRoutes.PairWatch.route) {
            PairWatchScreen(navController = navController)
        }
        composable(NavRoutes.PushUpCounter.route) {
            PushUpCounterScreen(navController = navController)
        }
    }
}


