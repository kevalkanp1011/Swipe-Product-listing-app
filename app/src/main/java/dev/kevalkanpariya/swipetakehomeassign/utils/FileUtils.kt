package dev.kevalkanpariya.swipetakehomeassign.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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

fun getFileNameFromUri(uri: Uri): String? {
    val path = uri.path ?: return null
    val lastSlashIndex = path.lastIndexOf('/')
    return if (lastSlashIndex != -1) {
        path.substring(lastSlashIndex + 1)
    } else {
        path
    }
}

@SuppressLint("Range")
fun getFileNameFromUri2(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme.equals("content")) {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

