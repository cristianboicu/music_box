package com.cristianboicu.musicbox.other

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.cristianboicu.musicbox.R
import java.io.FileDescriptor
import java.io.IOException


object Util {
    fun convertMsToMinutes(ms: Int): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainedSeconds = (ms - (minutes * 60 * 1000)) / 1000

        return if (remainedSeconds < 10) {
            "${minutes}:0${remainedSeconds}"
        } else {
            "${minutes}:${remainedSeconds}"
        }
    }

    fun uriToBitmap(context: Context, selectedFileUri: Uri?): Bitmap? {
        selectedFileUri?.let {
            try {
                val parcelFileDescriptor: ParcelFileDescriptor? =
                    context.contentResolver.openFileDescriptor(it, "r")
                val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor?.close()
                return image
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.default_cover_art)
    }
}