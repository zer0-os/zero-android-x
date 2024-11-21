package io.element.android.support.zero.screens.verifysession.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun TextBackupActionRequired(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = ElementTheme.colors.iconCriticalPrimary
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = text,
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            color = ElementTheme.colors.iconCriticalPrimary
        )
    }
}

@Composable
fun TextBackupActionDone(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ElementTheme.colors.zeroBrandColor
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = text,
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            color = ElementTheme.colors.zeroBrandColor
        )
    }
}
