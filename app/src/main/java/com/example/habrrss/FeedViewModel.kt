package com.example.habrrss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel: ViewModel() {
    private val feedFlowMutable = MutableStateFlow(emptyList<FeedItem>())
    val feedFlow: StateFlow<List<FeedItem>>
        get() = feedFlowMutable

    private val feedErrorChannel = Channel<FeedError>()
    val feedError: Flow<FeedError>
        get() = feedErrorChannel.consumeAsFlow()

    private val repository = FeedRepository()

    init {
        rss()
    }

    fun rss() {
        viewModelScope.launch {
            getRss()
        }
    }

    fun gotFeedError() {
        viewModelScope.launch {
            feedErrorChannel.send(FeedError.OK)
        }
    }

    private suspend fun getRss() {
        withContext(Dispatchers.IO) {
            runCatching {
                repository.getRss()
            }.onSuccess {
                if (it != null) {
                    feedFlowMutable.emit(it)
                    feedErrorChannel.send(FeedError.OK)
                } else
                    feedErrorChannel.send(FeedError.PARSING)
            }.onFailure {
                feedErrorChannel.send(FeedError.NETWORKING)
            }

        }
    }
}
