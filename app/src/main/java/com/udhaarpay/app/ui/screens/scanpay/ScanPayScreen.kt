package com.udhaarpay.app.ui.screens.scanpay

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import com.udhaarpay.app.ui.screens.nfc.NFCPaymentScreen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import java.io.InputStream

@Composable
fun ScanPayScreen() {
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var galleryImageUri by remember { mutableStateOf<Uri?>(null) }
    var showNfcDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        galleryImageUri = uri
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            inputStream?.use { stream ->
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                val result = decodeQrFromBitmap(bitmap)
                scannedResult = result
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scan & Pay", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(18.dp))
        Button(onClick = { galleryLauncher.launch("image/*") }, shape = RoundedCornerShape(12.dp)) {
            Text("Scan QR from Gallery")
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { showNfcDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Icon(Icons.Default.Contactless, contentDescription = "NFC", tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Pay with NFC", color = Color.White)
        }
        Spacer(Modifier.height(24.dp))
        scannedResult?.let {
            Text("Result: $it", color = MaterialTheme.colorScheme.secondary)
        }
    }

    if (showNfcDialog) {
        Dialog(onDismissRequest = { showNfcDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Box(Modifier.sizeIn(minWidth = 340.dp, minHeight = 480.dp)) {
                    NFCPaymentScreen(onBack = { showNfcDialog = false })
                }
            }
        }
    }
}

// Stub for missing QR decode function
fun decodeQrFromBitmap(bitmap: android.graphics.Bitmap?): String {
    // TODO: Implement actual QR decoding using ZXing or similar
    return if (bitmap != null) "QR code detected (mock)" else "Failed to decode QR"
}