package com.example.udhaarpay.ui.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Minimal stub to satisfy usages of hiltViewModel() in composables when Hilt-backed retrieval
// isn't required for compilation. This returns a regular ViewModel instance via Compose viewModel().
@Composable
inline fun <reified VM : ViewModel> hiltViewModel(): VM {
    return viewModel()
}
