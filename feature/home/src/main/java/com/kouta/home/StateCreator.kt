package com.kouta.home

import com.kouta.auth.User
import javax.inject.Inject

class StateCreator @Inject constructor() {
    fun create(
        isLogin: Boolean,
        user: User?
    ) = UiState(
        isLogin = isLogin,
        user = user
    )
}