package com.example.trainchecker.screens

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InputScreen(navController: NavController, sharedPreferences: SharedPreferences) {
    var name by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("name", "") ?: "")) }
    var weight by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("weight", "") ?: "")) }
    var height by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("height", "") ?: "")) }
    var intensity by remember { mutableStateOf(sharedPreferences.getString("intensity", "Low") ?: "Low") }
    var expanded by remember { mutableStateOf(false) }

    val intensityOptions = listOf("Low", "Medium", "High")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Training Level")
        Box {
            Button(onClick = { expanded = true }) {
                Text(intensity)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                intensityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            intensity = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (name.text.isNotEmpty() && weight.text.isNotEmpty() && height.text.isNotEmpty()) {
                saveUserData(sharedPreferences, name.text, weight.text, height.text, intensity)
                navController.navigate("settings")
            }
        }) {
            Text("Submit")
        }
    }
}

fun saveUserData(sharedPreferences: SharedPreferences, name: String, weight: String, height: String, intensity: String) {
    val editor = sharedPreferences.edit()
    editor.putString("name", name)
    editor.putString("weight", weight)
    editor.putString("height", height)
    editor.putString("intensity", intensity)
    editor.apply()
}
