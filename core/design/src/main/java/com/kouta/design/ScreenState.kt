package com.kouta.design

sealed class ScreenState<out T: Any?> {
    data object None : ScreenState<Nothing>()

    sealed class Loading<out T: Any?> : ScreenState<T>() {
        data object Initial : Loading<Nothing>()
        data class Refreshing<out T: Any?>(val data: T?) : Loading<T>()
    }

    sealed class Fetched<out T: Any?> : ScreenState<T>() {
        data object ZeroMatch : Fetched<Nothing>()
        data class Success<out T: Any?>(val data: T?) : Fetched<T>()
    }

    data object NotLogin : ScreenState<Nothing>()

    data class Error(val message: String) : ScreenState<Nothing>()
}