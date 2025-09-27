package com.example.friiomain.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun bitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
}

fun base64ToBitmap(base64: String): Bitmap? {
    val bytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
