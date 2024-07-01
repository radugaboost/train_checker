package com.example.trainchecker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trainchecker.screens.ExerciseDetailScreen
import com.example.trainchecker.screens.SettingsScreen
import com.example.trainchecker.screens.TrainingsScreen
import com.example.trainchecker.screens.WorkoutDetailScreen
import com.example.trainchecker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val localeViewModel: LocaleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val isDarkTheme = mutableStateOf(false)

        setContent {
            AppTheme(useDarkTheme = isDarkTheme.value) {
                val navController = rememberNavController()
                val context = LocalContext.current
                val locale by localeViewModel.locale

                CompositionLocalProvider(
                    LocalContext provides
                        context.createConfigurationContext(
                            android.content.res.Configuration().apply {
                                setLocale(locale)
                            },
                        ),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        NavHost(
                            navController,
                            startDestination = if (isUserDataSaved()) "trainings" else "settings",
                        ) {
                            composable("settings") {
                                SettingsScreen(navController, sharedPreferences, isDarkTheme, localeViewModel)
                            }
                            composable("trainings") {
                                TrainingsScreen(navController, locale)
                            }
                            composable("workoutDetail/{workoutJson}") { backStackEntry ->
                                val workoutJson = backStackEntry.arguments?.getString("workoutJson")
                                WorkoutDetailScreen(navController, workoutJson)
                            }
                            composable("exerciseDetail/{exercisesJson}") { backStackEntry ->
                                val exercisesJson = backStackEntry.arguments?.getString("exercisesJson")
                                ExerciseDetailScreen(navController, exercisesJson)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isUserDataSaved(): Boolean {
        val name = sharedPreferences.getString("name", null)
        val weight = sharedPreferences.getString("weight", null)
        val height = sharedPreferences.getString("height", null)
        return name != null && weight != null && height != null
    }
}
