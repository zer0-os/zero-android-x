/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.HomeState
import io.element.android.features.home.impl.HomeStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.toUri
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray

@Composable
fun WalletReceiveTokenSheet(
    modifier: Modifier = Modifier,
    state: HomeState,
    onDismissSheet: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val qrCodeUri: MutableState<Uri?> = remember { mutableStateOf(null) }

    val copyAddress: () -> Unit = {
        state.matrixUser.walletAddress?.let {
            clipboardManager.setText(AnnotatedString(it))
        }
    }
    val shareQRCode: () -> Unit = {
        qrCodeUri.value?.let { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgCanvasDefault)
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Receive",
                style = ElementTheme.typography.fontBodyLgMedium,
                textAlign = TextAlign.Center
            )

            IconButton(onClick = onDismissSheet) {
                Icon(
                    imageVector = CompoundIcons.Close(),
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.matrixUser.displayName?.let { username ->
                Text(
                    text = username,
                    style = ElementTheme.typography.fontHeadingSmMedium
                )
            }
            ZeroWalletUtil.walletAddressDisplayText(state.matrixUser.walletAddress)?.let { walletAddress ->
                Text(
                    text = walletAddress,
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }
            state.matrixUser.walletAddress?.let { address ->
                WalletAddressQRCode(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    walletAddress = address,
                    onQRCodeRendered = { uri ->
                        qrCodeUri.value = uri
                    }
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, ElementTheme.colors.iconSecondary, RoundedCornerShape(8.dp)),
                onClick = copyAddress
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = CompoundIcons.Copy(),
                    contentDescription = null,
                    tint = ElementTheme.colors.iconSecondary
                )
            }

            Spacer(Modifier.size(16.dp))

            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, ElementTheme.colors.iconSecondary, RoundedCornerShape(8.dp)),
                onClick = shareQRCode
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = CompoundIcons.Share(),
                    contentDescription = null,
                    tint = ElementTheme.colors.iconSecondary
                )
            }
        }
    }
}

@Composable
fun WalletAddressQRCode(
    modifier: Modifier = Modifier,
    walletAddress: String,
    onQRCodeRendered: (Uri) -> Unit
) {
    val context = LocalContext.current
    val logoPainter: Painter = painterResource(R.drawable.zero_logo_icon_small_black)
    val qrcodePainter : Painter = rememberQrCodePainter(walletAddress) {
        logo {
            painter = logoPainter
            padding = QrLogoPadding.Natural(.1f)
            shape = QrLogoShape.circle()
            size = 0.2f
        }

        shapes {
            ball = QrBallShape.circle()
            darkPixel = QrPixelShape.circle()
            frame = QrFrameShape.circle()
        }

        colors {
            dark = QrBrush.solid(Color.Black)
            frame = QrBrush.solid(Color.Black)
            ball = QrBrush.solid(Color.Black)
        }
    }

    LaunchedEffect(Unit) {
        val bytes: ByteArray = qrcodePainter.toByteArray(1024, 1024, ImageFormat.PNG)
        val qrCodeUri = bytes.toUri(context, "qr-code.png")
        onQRCodeRendered(qrCodeUri)
    }

    Image(
        modifier = modifier
            .size(200.dp)
            .background(
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp),
        painter = qrcodePainter,
        contentDescription = null
    )
}

@PreviewsDayNight
@Composable
fun WalletReceiveTokenSheetPreview(
    @PreviewParameter(HomeStateProvider::class) state: HomeState
) = ElementPreview {
    WalletReceiveTokenSheet(state = state)
}
