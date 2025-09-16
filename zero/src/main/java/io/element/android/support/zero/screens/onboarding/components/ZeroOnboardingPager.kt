/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.screens.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.theme.SPACING_6X
import kotlinx.coroutines.launch

@Composable
fun ZeroOnboardingPager(
    modifier: Modifier = Modifier,
    pagerSize: Dp = 350.dp
) {
    val imagesPainter = listOf<Painter>(
        painterResource(R.drawable.img_onboarding_leaf1),
        painterResource(R.drawable.img_onboarding_leaf2),
        painterResource(R.drawable.img_onboarding_leaf3),
        painterResource(R.drawable.img_onboarding_leaf4),
    )
    val pagerState = rememberPagerState(pageCount = { imagesPainter.size })
    val coroutineScope = rememberCoroutineScope()

    val pageTitle: String = when (pagerState.currentPage) {
        0 -> "Make internet money."
        1 -> "Securely chat with friends and family."
        2 -> "Buy, sell, and swap millions of coins."
        else -> "Work for yourself and own your future."
    }
    val pageSubTitle = when (pagerState.currentPage) {
        0 -> "Turn your ideas into money with a platform that rewards creativity."
        1 -> "Stay connected with those who matter most, without compromise."
        2 -> "Seamless, secure trading with deep liquidity and instant execution."
        else -> "Turn your ideas into money with a platform that rewards creativity."
    }

    Column(modifier = modifier) {
        Text(
            text = pageTitle,
            style = ElementTheme.zeroTypography.fontHeadingMdRegular,
            color = ElementTheme.colors.zeroBrandColor
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = pageSubTitle,
            style = ElementTheme.zeroTypography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary
        )

        Spacer(Modifier.size(SPACING_6X.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerSize)
        ) { page ->
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = imagesPainter[page],
                contentDescription = "pager_image_$page"
            )
        }

        Spacer(Modifier.size(SPACING_6X.dp))

        DotIndicator(
            pageCount = imagesPainter.size,
            currentPage = pagerState.currentPage,
            onDotClicked = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        Spacer(Modifier.size(SPACING_6X.dp))
    }
}

@Composable
private fun DotIndicator(
    pageCount: Int,
    currentPage: Int,
    onDotClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.bgCanvasDefaultLevel1,
                animationSpec = tween(durationMillis = 250)
            )

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(animatedColor)
                    .clickable { onDotClicked(i) }
            )

            if (i != pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun ZeroOnboardingPagerPreview() = ElementPreview {
    ZeroOnboardingPager()
}
