package com.example.w2_clean_example

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray? {
    val stream = ByteArrayOutputStream()

    compress(Bitmap.CompressFormat.JPEG, 50, stream)
    return stream.toByteArray()
}