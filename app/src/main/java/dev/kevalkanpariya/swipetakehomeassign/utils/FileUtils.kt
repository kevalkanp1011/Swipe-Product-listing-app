package dev.kevalkanpariya.swipetakehomeassign.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun createTempImageFile(context: Context): File {
    val storageDir = context.cacheDir
    return File.createTempFile("temp_image_", ".jpg", storageDir)
}

fun getUriForFile(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun deleteFile(file: File): Boolean {
    return file.delete()
}

fun createFileFromInputStream(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("tempFile", ".jpg", context.cacheDir) // Change the file extension if necessary

    inputStream?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}

