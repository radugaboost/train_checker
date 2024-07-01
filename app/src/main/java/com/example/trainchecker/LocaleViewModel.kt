package com.example.trainchecker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import java.util.Locale

class LocaleViewModel : ViewModel() {
    private val _locale = mutableStateOf(Locale.getDefault())
    val locale: State<Locale> get() = _locale

    fun setLocale(newLocale: Locale) {
        _locale.value = newLocale
    }
}