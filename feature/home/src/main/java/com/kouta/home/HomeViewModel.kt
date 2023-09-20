package com.kouta.home

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kouta.home.repository.AccountLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
//    private val stateCreator: StateCreator,
//    private val accountLoginRepository: AccountLoginRepository
) : ViewModel() {

    val uiState: StateFlow<UiState> =
        emptyFlow<UiState>().stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun login(launcher: ActivityResultLauncher<Intent>) {
//        accountLoginRepository.login(launcher)
    }
}