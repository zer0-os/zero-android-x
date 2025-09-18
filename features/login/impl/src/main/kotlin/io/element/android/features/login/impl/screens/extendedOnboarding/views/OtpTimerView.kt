/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpTimerView(
    duration: Int = 30,
    onResendClick: () -> Unit = {}
) {
    var timer by remember { mutableIntStateOf(duration) }
    var isRunning by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // countdown effect
    LaunchedEffect(isRunning) {
        if (isRunning) {
            timer = duration
            while (timer > 0) {
                delay(1000L)
                timer--
            }
            isRunning = false
        }
    }

    TextButton(
        text = if (isRunning) "Resend in $timer" else "Resend OTP",
        onClick = {
            onResendClick()
            scope.launch {
                delay(2000) //Restart timer after 2 seconds
                isRunning = true
            }
        },
        size = ButtonSize.Medium,
        enabled = !isRunning,
        contentColor = if (isRunning)
            ElementTheme.colors.textSecondary
        else
            ElementTheme.colors.zeroBrandColor
    )
}
