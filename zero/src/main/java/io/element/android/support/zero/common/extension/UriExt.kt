/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.extension

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun Uri.localFile(context: Context): File? {
    val contentResolver = context.contentResolver
    val fileName = this.localFileName(context) ?: return null
    val file = File(context.cacheDir, fileName)
    try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun Uri.localFileName(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return null
}

fun ByteArray.toUri(context: Context, fileName: String): Uri {
    // Save the bytes as a temp file
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use {
        it.write(this)
        it.flush()
    }
    // Get content URI
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "com.zero.android.messenger.fileprovider",
        file
    )
    return uri
}
