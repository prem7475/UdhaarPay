package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.model.MarketNewsItem
import com.udhaarpay.app.repository.MarketNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketNewsViewModel @Inject constructor(
    private val repository: MarketNewsRepository
) : ViewModel() {

    private val _news = MutableStateFlow<List<MarketNewsItem>>(emptyList())
    val news: StateFlow<List<MarketNewsItem>> = _news.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _lastUpdatedAt = MutableStateFlow<Long?>(null)
    val lastUpdatedAt: StateFlow<Long?> = _lastUpdatedAt.asStateFlow()

    init {
        refreshNews()
        viewModelScope.launch {
            while (true) {
                delay(120000L)
                if (!_isLoading.value) {
                    refreshNews()
                }
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null

            val result = repository.fetchLatestMarketNews()
            result.onSuccess { items ->
                _news.value = items
                _lastUpdatedAt.value = System.currentTimeMillis()
                if (items.isEmpty()) {
                    _statusMessage.value = "No latest market updates found right now."
                }
            }.onFailure { throwable ->
                if (_news.value.isEmpty()) {
                    _statusMessage.value = throwable.message ?: "Unable to load market news."
                } else {
                    _statusMessage.value = "Showing cached list. Pull refresh for latest updates."
                }
            }

            _isLoading.value = false
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}
