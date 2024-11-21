package io.element.android.support.zero.common.extension

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Int.toThousandSpacedString(): String {
    return try {
        when {
            this in 1..999 -> String.format("%d", this)
            this > 999 -> {
                (this / 1000).toThousandSpacedString() + String.format(",%03d", this % 1000)
            }
            else -> "0"
        }
    } catch (e: Exception) {
        String.format("%d", this)
    }
}
