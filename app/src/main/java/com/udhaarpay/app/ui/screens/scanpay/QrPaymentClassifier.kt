package com.udhaarpay.app.ui.screens.scanpay

import android.net.Uri
import java.util.Locale

data class ParsedQrPayment(
    val raw: String,
    val upiId: String?,
    val payeeName: String?,
    val merchantCategory: String,
    val merchantCode: String?,
    val isMerchant: Boolean,
    val amount: Double?,
    val note: String?
)

fun parseUpiQrPayload(rawInput: String): ParsedQrPayment {
    val raw = rawInput.trim()
    if (raw.isBlank()) {
        return ParsedQrPayment(
            raw = "",
            upiId = null,
            payeeName = null,
            merchantCategory = "Unknown",
            merchantCode = null,
            isMerchant = false,
            amount = null,
            note = null
        )
    }

    val lower = raw.lowercase(Locale.getDefault())
    val uri = runCatching { Uri.parse(raw) }.getOrNull()
    val queryNames = uri?.queryParameterNames ?: emptySet()
    val query = queryNames.associateWith { name -> uri?.getQueryParameter(name) }

    val upiId = query["pa"]?.trim()?.takeIf { it.isNotBlank() }
        ?: when {
            lower.startsWith("upi://pay") || lower.startsWith("upi:") -> query["pa"]?.trim()?.takeIf { it.isNotBlank() }
            raw.contains("@") && !raw.contains(" ") -> raw
            else -> null
        }

    val name = query["pn"]?.trim()?.ifBlank { null }
    val merchantCode = query["mc"]?.trim()?.ifBlank { null }
    val amount = query["am"]?.toDoubleOrNull()
    val note = query["tn"]?.trim()?.ifBlank { null }
    val sourceHint = query["tr"]?.trim()?.ifBlank { null }

    val merchantCategory = resolveMerchantCategory(merchantCode, name, sourceHint, raw)
    val isMerchant = merchantCode != null || query.containsKey("tid") || merchantCategory != "Personal"

    return ParsedQrPayment(
        raw = raw,
        upiId = upiId,
        payeeName = name,
        merchantCategory = merchantCategory,
        merchantCode = merchantCode,
        isMerchant = isMerchant,
        amount = amount,
        note = note
    )
}

private fun resolveMerchantCategory(
    merchantCode: String?,
    merchantName: String?,
    sourceHint: String?,
    raw: String
): String {
    val code = merchantCode?.takeIf { it.isNotBlank() }
    if (code != null) {
        return when (code) {
            "5411", "5422", "5441", "5451", "5462", "5499" -> "Food & Grocery"
            "5812", "5814" -> "Dining"
            "4111", "4511", "4722" -> "Travel"
            "5211", "5311", "5399", "5999" -> "Shopping"
            "4814", "4816", "4829", "4899" -> "Bills & Telecom"
            "4112", "4121" -> "Transit"
            "4900" -> "Utilities"
            "5732" -> "Electronics"
            "6011", "6012" -> "Bank Transfer"
            else -> "Merchant"
        }
    }

    val name = listOfNotNull(merchantName, sourceHint, raw)
        .joinToString(" ")
        .lowercase(Locale.getDefault())
    return when {
        name.contains("metro") || name.contains("bus") || name.contains("rail") || name.contains("uber") || name.contains("ola") -> "Travel"
        name.contains("food") || name.contains("cafe") || name.contains("restaurant") || name.contains("swiggy") || name.contains("zomato") -> "Dining"
        name.contains("mall") || name.contains("shop") || name.contains("retail") || name.contains("flipkart") || name.contains("amazon") -> "Shopping"
        name.contains("bill") || name.contains("electric") || name.contains("water") || name.contains("mobile") || name.contains("recharge") -> "Bills & Telecom"
        name.contains("self") || name.contains("friend") || name.contains("personal") -> "Personal"
        raw.contains("@", ignoreCase = true) -> "Personal"
        else -> "Merchant"
    }
}
