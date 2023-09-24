package com.kouta.home

import com.kouta.auth.User

data class UiState(
    val user: User? = null,
    val isLogin: Boolean = false,
    val isShowLoading: Boolean = false
)
