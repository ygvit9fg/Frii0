package com.example.friiomain.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

fun generateQrCode(content: String): Bitmap? {
    return try {
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400)
    } catch (e: Exception) {
        null
    }
}