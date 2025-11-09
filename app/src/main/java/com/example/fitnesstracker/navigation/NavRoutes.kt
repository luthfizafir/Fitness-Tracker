package com.example.fitnesstracker.navigation

sealed class NavRoutes(val route: String) {
    object Welcome : NavRoutes("welcome")
    object Login : NavRoutes("login")
    object CreateAccount : NavRoutes("create_account")
    object AppIntroduction : NavRoutes("app_introduction")
    object PermissionsRequest : NavRoutes("permissions_request")
    object Tutorial : NavRoutes("tutorial")
    object Home : NavRoutes("home")
    object SelectWorkout : NavRoutes("select_workout")
    object CustomizeSession : NavRoutes("customize_session")
    object Countdown : NavRoutes("countdown")
    object WorkoutSession : NavRoutes("workout_session")
    object SessionSummary : NavRoutes("session_summary")
    object History : NavRoutes("history")
    object SessionDetails : NavRoutes("session_details")
    object Settings : NavRoutes("settings")
    object Profile : NavRoutes("profile")
    object AppPreferences : NavRoutes("app_preferences")
    object TutorialHelp : NavRoutes("tutorial_help")
    object PairWatch : NavRoutes("pair_watch")
    object PushUpCounter : NavRoutes("push_up_counter")
}


