/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.support.zero.common.extension.openExternalUri

@Composable
fun AppForceUpdateView() {
    val context: Context = LocalContext.current

    val openAppLink: () -> Unit = {
        val appUrl = "market://details?id=${BuildConfig.APPLICATION_ID}"
        context.openExternalUri(appUrl)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgCanvasDefault)
            .imePadding()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(io.element.android.support.zero.R.drawable.img_app_update),
                contentDescription = null
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "Update Required",
                style = ElementTheme.typography.fontHeadingMdBold,
                color = ElementTheme.colors.zeroBrandColor
            )
            Spacer(Modifier.size(2.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "To continue using the app, please update to the latest version.",
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
        UpdateNowButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            onTap = openAppLink
        )
    }
}

@Composable
fun UpdateNowButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {}
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor
        ),
        onClick = onTap
    ) {
        Text(
            "Update Now",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@PreviewsDayNight
@Composable
fun AppForceUpdateViewPreview() = ElementPreview {
    AppForceUpdateView()
}
