package io.element.android.support.zero.screens.verifysession

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.OutlinedButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.component.ZImageButton
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_3X
import io.element.android.support.zero.screens.verifysession.components.TextBackupActionDone
import io.element.android.support.zero.screens.verifysession.components.TextBackupActionRequired

@Composable
fun ZeroVerifySelfSessionView(
    onSkipVerification: () -> Unit = {},
    onEnterRecoveryKey: () -> Unit = {},
    onResetRecoveryKey: () -> Unit = {},
) {
    var showBackModalCancellation: Boolean by remember { mutableStateOf(false) }

    if (showBackModalCancellation) {
        BackupModalCancellationDialog(
            onContinue = {
                showBackModalCancellation = false
                onSkipVerification()
            },
            onDismiss = {
                showBackModalCancellation = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.img_zero_account_backup_header),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = {
                    showBackModalCancellation = true
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.DarkGray, shape = CircleShape)
            ) {
                Icon(
                    imageVector = CompoundIcons.Close(),
                    contentDescription = "",
                )
            }
        }

        Column(modifier = Modifier.padding(32.dp)) {
            Text(
                text = "Verify Login",
                style = ElementTheme.zeroTypography.fontHeadingSmMedium,
                color = ElementTheme.colors.textPrimary
            )

            Spacer(Modifier.size(SPACING_3X.dp))
            Text(
                text = "Access your encrypted messages between devices and logins with an account backup.",
                style = ElementTheme.zeroTypography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.size(SPACING_10X.dp))
            TextBackupActionDone(text = "Your account has a backup phrase")
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            TextBackupActionRequired(text = "Your current login is not verified, some message history may be hidden")

            Spacer(modifier = Modifier.size(SPACING_10X.dp))
            Spacer(modifier = Modifier.size(SPACING_10X.dp))
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                ZImageButton(
                    image = R.drawable.img_btn_verify_backup,
                    text = "Verify with backup phrase",
                    onClick = onEnterRecoveryKey
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                text = "Forgot recovery key?",
                onClick = { onResetRecoveryKey() },
            )
        }
    }
}

@Composable
fun BackupModalCancellationDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    val positiveButtonText = "Verify Later"
    val message = "You have not verified this login, messages from past conversations may be hidden."
    ZeroAlertDialog(
        title = "Are you sure?",
        message = message,
        onDismiss = onDismiss,
        confirmButton = {
            TextButton(onClick = onContinue) {
                androidx.compose.material3.Text(text = positiveButtonText, color = ElementTheme.colors.iconCriticalPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text(text = "Cancel", color = ElementTheme.colors.textPrimary)
            }
        }
    )
}

@PreviewsDayNight
@Composable
fun ZeroVerifySelfSessionViewPreview() = ElementPreview {
    ZeroVerifySelfSessionView()
}
