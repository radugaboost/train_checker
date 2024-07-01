package com.example.trainchecker.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trainchecker.R
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale

data class Workout(
    val name: String = "",
    val muscles: String = "",
    val storagePath: String = "",
    val exercises: List<Exercise> = listOf(),
)

data class Exercise(
    val name: String = "",
    val repetitions: Int = 0,
    val sets: Int = 0,
    val description: String = "",
    val image: String = "",
)

@Composable
fun TrainingsScreen(
    navController: NavController,
    locale: Locale,
) {
    var workouts by remember { mutableStateOf(listOf<Workout>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        workouts = fetchWorkoutsFromStorage(locale)
        isLoading = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    navController.navigate("settings")
                },
                modifier = Modifier.padding(end = 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                )
            }
        }

        Text(
            text = stringResource(R.string.trainings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (workouts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.trainings_not_found),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp),
            ) {
                workouts.forEach { workout ->
                    WorkoutItem(workout = workout, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(
    workout: Workout,
    navController: NavController,
) {
    val workoutJson = URLEncoder.encode(Gson().toJson(workout), "UTF-8")

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(50))
                .padding(16.dp)
                .clickable {
                    navController.navigate("workoutDetail/$workoutJson")
                },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = workout.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = workout.muscles,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.surface,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

suspend fun fetchWorkoutsFromStorage(locale: Locale): List<Workout> {
    val storage = FirebaseStorage.getInstance()
    val workoutsRef = storage.reference.child("workouts")
    val workouts = mutableListOf<Workout>()
    val maxDownloadFileSizeBytes: Long = 120000

    try {
        val files = workoutsRef.listAll().await()
        files.items.forEach { fileRef ->
            val fileContent = fileRef.getBytes(maxDownloadFileSizeBytes).await()
            val jsonString = String(fileContent)
            val workout = parseWorkoutFromJson(jsonString, locale)
            workouts.add(workout)
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Error loading workouts", e)
    }

    return workouts
}

fun parseWorkoutFromJson(
    jsonString: String,
    locale: Locale,
): Workout {
    val languageKey = if (locale.language == "ru") "ru" else "en"
    val localizedJson = JSONObject(jsonString).getJSONObject(languageKey).toString()
    return Gson().fromJson(localizedJson, Workout::class.java)
}
