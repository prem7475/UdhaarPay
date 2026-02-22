package com.udhaarpay.app.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InAppBanner(
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isCredit: Boolean = false
)

object InAppBannerManager {
    private val _banner = MutableStateFlow<InAppBanner?>(null)
    val banner: StateFlow<InAppBanner?> = _banner.asStateFlow()

    fun show(banner: InAppBanner) {
        _banner.value = banner
    }

    fun clear() {
        _banner.value = null
    }
}

