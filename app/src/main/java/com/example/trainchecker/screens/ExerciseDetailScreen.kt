package com.example.trainchecker.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.trainchecker.R
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.net.URLDecoder

@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exercisesJson: String?,
) {
    val decodedExercisesJson = URLDecoder.decode(exercisesJson ?: "", "UTF-8")
    val exercises = Gson().fromJson(decodedExercisesJson, Array<Exercise>::class.java).toList()

    var currentSet by remember { mutableIntStateOf(1) }
    var completedSets by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableIntStateOf(0) }
    var idx by remember { mutableIntStateOf(0) }
    var exercise by remember { mutableStateOf(exercises[idx]) }
    var showNextSetButton by remember { mutableStateOf(false) }
    var showNextExerciseButton by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            timerSeconds++
        }
    }

    LaunchedEffect(exercise.image) {
        getFirebaseImageUrl(exercise.image) { url ->
            imageUrl = url
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = exercise.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (imageUrl != null) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                contentDescription = "image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = exercise.description,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.completed_sets, completedSets, exercise.sets),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isTimerRunning) {
            Text(
                text = stringResource(id = R.string.stopwatch, timerSeconds),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isTimerRunning && !(idx == exercises.size - 1 && exercise.sets == currentSet)) {
            Button(
                onClick = {
                    if (!isTimerRunning) {
                        isTimerRunning = true
                        timerSeconds = 0
                    }
                    showNextSetButton = true
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(id = R.string.start_exercise))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showNextSetButton) {
            Button(
                onClick = {
                    if (currentSet < exercise.sets) {
                        currentSet++
                        completedSets++
                    } else {
                        showNextSetButton = false
                        if (idx < exercises.size - 1) {
                            idx++
                            exercise = exercises[idx]
                            currentSet = 1
                            completedSets = 0
                            isTimerRunning = false
                            showNextSetButton = false
                            showNextExerciseButton = false
                        } else {
                            showNextExerciseButton = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                if (currentSet < exercise.sets) {
                    Text(stringResource(id = R.string.next_set))
                } else {
                    Text(stringResource(id = R.string.finish_exercise))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showNextExerciseButton) {
            Button(
                onClick = {
                    if (idx < exercises.size - 1) {
                        idx++
                        exercise = exercises[idx]
                        currentSet = 1
                        completedSets = 0
                        isTimerRunning = false
                        showNextSetButton = true
                        showNextExerciseButton = false
                    } else {
                        showNextSetButton = false
                        showNextExerciseButton = false
                        navController.navigate("trainings")
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                if (idx == exercises.size - 1) {
                    Text(stringResource(id = R.string.finish_workout))
                    isTimerRunning = false
                } else {
                    Text(stringResource(id = R.string.next_exercise))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun getFirebaseImageUrl(
    imagePath: String,
    onComplete: (String) -> Unit,
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child(imagePath)

    storageRef.downloadUrl.addOnSuccessListener { uri ->
        onComplete(uri.toString())
    }.addOnFailureListener {
        Log.e("Firebase", "Error loading image")
    }
}
