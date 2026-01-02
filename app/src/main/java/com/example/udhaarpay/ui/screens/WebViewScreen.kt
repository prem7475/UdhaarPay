package com.example.udhaarpay.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.udhaarpay.ui.theme.PureBlack
import com.example.udhaarpay.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text(title, color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            },
            update = { webView ->
                webView.loadUrl(url)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
