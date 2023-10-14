package com.kouta.home.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.usecase.video.GetSubscriptionVideoUseCase
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.extension.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
@HiltViewModel
class FavoriteChannelContentsViewModel @Inject constructor(
    private val stateCreator: StateCreator,
    private val getSubscriptionVideoUseCase: GetSubscriptionVideoUseCase
) : ViewModel() {
    sealed class Action {
        data object OnClickBack : Action()
        data class OnClickFilter(val filter: LiveBroadcastContent) : Action()
    }

    sealed class ViewEvent {
        data object PopBackStack : ViewEvent()
    }

    private val _viewEvent: Channel<ViewEvent> = Channel()
    val viewEvent = _viewEvent.receiveAsFlow()

    private val filterFlow: MutableStateFlow<LiveBroadcastContent> = MutableStateFlow(LiveBroadcastContent.UNKNOWN)

    private val itemFlow: StateFlow<Flow<PagingData<SubscriptionVideo>>> = filterFlow.map {
        getSubscriptionVideoUseCase.get(viewModelScope, it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyFlow())


    val uiState = itemFlow.combine(filterFlow) { itemFlow, filtersFlow ->
        stateCreator.create(itemFlow, filtersFlow)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    val dispatch: (Action) -> Unit = {
        launch {
            when (it) {
                Action.OnClickBack -> popBackStack()
                is Action.OnClickFilter -> updateFiltersFlow(it.filter)
            }
        }
    }

    private suspend fun popBackStack() {
        _viewEvent.send(ViewEvent.PopBackStack)
    }

    private suspend fun updateFiltersFlow(filter: LiveBroadcastContent) {
        if (filter == filterFlow.value) {
            filterFlow.emit(LiveBroadcastContent.UNKNOWN)
        } else {
            filterFlow.emit(filter)
        }
    }
}

