package androidx.hilt.navigation.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Minimal shim so `import androidx.hilt.navigation.compose.hiltViewModel` resolves
@Composable
inline fun <reified VM : ViewModel> hiltViewModel(): VM {
    // Delegate to the standard viewModel() as a safe fallback for compilation.
    return viewModel()
}
