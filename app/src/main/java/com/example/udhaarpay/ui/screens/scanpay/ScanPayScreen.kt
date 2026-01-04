package com.example.udhaarpay.ui.screens.scanpay

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scan & Pay", fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { /* TODO: Integrate camera QR scan */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Scan QR Code")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Add from Gallery")
        }
        Spacer(Modifier.height(16.dp))
        if (galleryImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(galleryImageUri),
                contentDescription = "QR from Gallery",
                modifier = Modifier.size(180.dp)
            )
        }
        if (scannedResult != null) {
            Spacer(Modifier.height(16.dp))
            Text("QR Data: ${scannedResult}", color = Color(0xFF059669))
            Button(onClick = { /* TODO: Proceed to payment with scannedResult */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Pay Now")
            }
        }
    }
}

fun decodeQrFromBitmap(bitmap: Bitmap?): String? {
    if (bitmap == null) return null
    val intArray = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        val result = MultiFormatReader().apply {
            setHints(mapOf(com.google.zxing.DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
        }.decode(binaryBitmap)
        result.text
    } catch (e: Exception) {
        null
    }
}
