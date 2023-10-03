package com.kouta.data.vo

sealed class ApiResponse<out T: Any?> {
    data class Success<out T: Any?>(val data: T): ApiResponse<T>()

    sealed class Error: ApiResponse<Nothing>() {
        data class Default(val message: String): Error()
        data object ParseException: Error()
    }

    fun isError() = this is Error
    suspend fun <T> ApiResponse<T>.onSuccess(action: suspend (T) -> Unit): ApiResponse<T>  {
        if (this is Success) {
            action(this.data)
        }
        return this
    }

    suspend fun <T> ApiResponse<T>.onError(action: suspend (Error) -> Unit): ApiResponse<T>  {
        if (this is Error) {
            action(this)
        }
        return this
    }
}
