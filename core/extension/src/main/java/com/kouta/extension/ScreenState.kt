package com.kouta.extension

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import timber.log.Timber

sealed class ScreenState<out T : Any?> {
    data object None : ScreenState<Nothing>()

    sealed class Loading<out T : Any?> : ScreenState<T>() {
        data object Initial : Loading<Nothing>()
//        data class Refreshing<out T: Any?>(val data: T?) : Loading<T>()
    }

    sealed class Fetched<out T : Any?> : ScreenState<T>() {
        data object ZeroMatch : Fetched<Nothing>()
        data class Success<out T : Any>(val data: T) : Fetched<T>()
    }

    data class Error(val message: String) : ScreenState<Nothing>()
}

fun <T : Any> LazyPagingItems<T>.toPagingScreenState(): ScreenState<LazyPagingItems<T>> {
    val refreshState = this.loadState.refresh
    Timber.d("ktakamat:screenState append=${loadState.append}")
    Timber.d("ktakamat:screenState prepend=${loadState.prepend}")
    Timber.d("ktakamat:screenState refresh=${loadState.refresh}")
    Timber.d("ktakamat:screenState source=${loadState.source}")
    Timber.d("ktakamat:screenState itemCount=${itemCount}\n")

    return when {
        refreshState is LoadState.Loading && this.itemCount == 0 -> ScreenState.Loading.Initial
        refreshState is LoadState.NotLoading && this.itemCount == 0 -> ScreenState.Fetched.ZeroMatch
        refreshState is LoadState.Error -> ScreenState.Error(
            refreshState.error.message ?: "何らかのエラーが発生しました"
        )

        this.itemCount > 0 -> ScreenState.Fetched.Success(this)
        else -> ScreenState.None
    }
}

fun <T : Any> LazyPagingItems<T>.isRefreshing() =
    this.loadState.refresh is LoadState.Loading && this.loadState.append is LoadState.NotLoading && itemCount > 0