package io.element.android.support.zero.common.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.support.zero.common.extension.innerShadow

@Composable
fun ZeroAlertDialog(
    title: String? = null,
    message: String,
    onDismiss: () -> Unit = {},
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        modifier =
        Modifier.innerShadow(color = Color(0x50F6F4F6), blur = 4.dp, cornersRadius = 28.dp),
        onDismissRequest = { onDismiss() },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title =
        if (!title.isNullOrBlank()) {
            { Text(text = title) }
        } else null,
        text = { Text(text = message) },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF28282C)
    )
}

@Composable
fun ZeroAlertDialog(
    title: @Composable (() -> Unit) ? = null,
    message: @Composable (() -> Unit),
    onDismiss: () -> Unit = {},
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        modifier =
            Modifier.innerShadow(color = Color(0x50F6F4F6), blur = 4.dp, cornersRadius = 28.dp),
        onDismissRequest = { onDismiss() },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = title,
        text = message,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF28282C)
    )
}
