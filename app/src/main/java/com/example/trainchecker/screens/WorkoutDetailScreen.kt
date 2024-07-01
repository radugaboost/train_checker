package com.example.trainchecker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutJson: String?,
) {
    val decodedWorkoutJson = URLDecoder.decode(workoutJson ?: "", "UTF-8")
    val workout = Gson().fromJson(decodedWorkoutJson, Workout::class.java)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = workout.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))
        workout.exercises.forEachIndexed { index, exercise ->
            ExerciseItem(exercise, navController, index, workout.exercises)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(
            onClick = {
                val exercisesJson = URLEncoder.encode(Gson().toJson(workout.exercises), "UTF-8")
                navController.navigate("exerciseDetail/$exercisesJson")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(stringResource(id = R.string.start_workout))
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    navController: NavController,
    index: Int,
    exercises: List<Exercise>,
) {
    val exercisesJson = URLEncoder.encode(Gson().toJson(exercises), "UTF-8")
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(50))
                .padding(16.dp)
                .clickable {
                    navController.navigate("exerciseDetail/$exercisesJson")
                },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = exercise.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(id = R.string.sets)}: ${exercise.sets}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.surface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(id = R.string.repetitions)}: ${exercise.repetitions}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.surface,
            )
        }
    }
}
