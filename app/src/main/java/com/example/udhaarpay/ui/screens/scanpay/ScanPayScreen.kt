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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.text.font.FontWeight
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
    var searchQuery by remember { mutableStateOf("") }
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
    // Mock contacts for search
    val contacts = listOf("Amit Sharma", "Priya Singh", "Rahul Verma", "Sneha Patel", "Rohit Kumar")
    val filteredContacts = contacts.filter { it.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scan & Pay", fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search contacts to pay") },
            modifier = Modifier.fillMaxWidth()
        )
        if (searchQuery.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7EF))
            ) {
                Column(Modifier.padding(8.dp)) {
                    filteredContacts.forEach { contact ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO: Pay to contact */ }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF2563EB))
                            Spacer(Modifier.width(10.dp))
                            Text(contact, fontSize = 16.sp, color = Color(0xFF1E293B))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { /* TODO: Integrate camera QR scan */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Scan QR Code")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Add from Gallery")
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFF2563EB), shape = MaterialTheme.shapes.medium)
                .clickable { /* TODO: Open scanner */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner", tint = Color.White, modifier = Modifier.size(80.dp))
            if (scannedResult != null) {
                Text("Scanned!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (galleryImageUri != null) {
            Spacer(Modifier.height(12.dp))
            Image(
                painter = rememberAsyncImagePainter(galleryImageUri),
                contentDescription = "QR from Gallery",
                modifier = Modifier.size(120.dp)
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
