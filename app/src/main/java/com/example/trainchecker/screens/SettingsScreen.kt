package com.example.trainchecker.screens

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trainchecker.LocaleViewModel
import com.example.trainchecker.R
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController, sharedPreferences: SharedPreferences, isDarkTheme: MutableState<Boolean>, localeViewModel: LocaleViewModel) {
    var name by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("name", "") ?: "")) }
    var weight by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("weight", "") ?: "")) }
    var height by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("height", "") ?: "")) }
    var intensity by remember { mutableStateOf(sharedPreferences.getString("intensity", "Low") ?: "Low") }

    var languageMenuExpanded by remember { mutableStateOf(false) }
    var intensityMenuExpanded by remember { mutableStateOf(false) }

    val languages = listOf("English", "Русский")
    val currentLocale = localeViewModel.locale.value
    val selectedLanguage = if (currentLocale.language == "ru") "Русский" else "English"
    val intensityOptions = listOf("Low", "Medium", "High")
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("trainings") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isDarkTheme.value = !isDarkTheme.value }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Toggle Theme",
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.6f)
                    .background(
                        MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                SettingTextInput(
                    name,
                    "name",
                    R.string.name,
                    KeyboardType.Ascii,
                    focusManager,
                    sharedPreferences
                ) { name = it }
                Spacer(modifier = Modifier.height(8.dp))
                SettingTextInput(
                    weight,
                    "weight",
                    R.string.weight,
                    KeyboardType.Decimal,
                    focusManager,
                    sharedPreferences
                ) { weight = it }
                Spacer(modifier = Modifier.height(8.dp))
                SettingTextInput(
                    height,
                    "height",
                    R.string.height,
                    KeyboardType.Decimal,
                    focusManager,
                    sharedPreferences
                ) { height = it }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.language)}: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.defaultMinSize(86.dp)
                    )
                    Box {
                        Button(onClick = { languageMenuExpanded = true }) {
                            Text(selectedLanguage)
                        }
                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            languages.forEach { language ->
                                DropdownMenuItem(
                                    onClick = {
                                        if (language == "English") {
                                            localeViewModel.setLocale(Locale.ENGLISH)
                                        } else {
                                            localeViewModel.setLocale(Locale("ru"))
                                        }
                                        languageMenuExpanded = false
                                    },
                                    text = { Text(language) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.intensity)}: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                    Box {
                        Button(onClick = { intensityMenuExpanded = true }) {
                            Text(intensity)
                        }
                        DropdownMenu(
                            expanded = intensityMenuExpanded,
                            onDismissRequest = { intensityMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            intensityOptions.forEach { option ->
                                DropdownMenuItem(
                                    onClick = {
                                        intensity = option
                                        saveData(sharedPreferences, "intensity", option)
                                        intensityMenuExpanded = false
                                    },
                                    text = { Text(option) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingTextInput(
    settingValue: TextFieldValue,
    key: String,
    resourceKey: Int,
    keyboardType: KeyboardType,
    focusManager: FocusManager,
    sharedPreferences: SharedPreferences,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${stringResource(resourceKey)}:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(80.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        TextField(
            value = settingValue,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    saveData(sharedPreferences, key, settingValue.text)
                }
            ),
            modifier = Modifier.weight(1f).height(54.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )
    }
}

fun saveData(sharedPreferences: SharedPreferences, key: String, value: String) {
    val editor = sharedPreferences.edit()
    editor.putString(key, value)
    editor.apply()
}