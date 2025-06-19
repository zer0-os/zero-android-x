package io.element.android.support.zero.common.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

fun Context.getActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.openExternalUri(url: String) {
    this.getActivity()?.let {
        try {
            val uri = url.toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            it.startActivity(intent)
        } catch (e: Exception) {
            showToast("You don't have an app to open this link")
        }
    }
}
