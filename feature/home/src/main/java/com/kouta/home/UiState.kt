package com.kouta.home

import com.kouta.auth.User

data class UiState(
    val isLogin: Boolean = false,
    val user: User? = null
)
