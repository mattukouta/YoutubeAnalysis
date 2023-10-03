package com.kouta.auth.vo

sealed class LoginState {
    data object NotLogin: LoginState()
    data class Login(val user: User): LoginState()
    data object Error: LoginState()
}