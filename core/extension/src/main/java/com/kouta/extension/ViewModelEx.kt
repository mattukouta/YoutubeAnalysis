package com.kouta.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun ViewModel.launch(action: suspend () -> Unit) = viewModelScope.launch { action() }