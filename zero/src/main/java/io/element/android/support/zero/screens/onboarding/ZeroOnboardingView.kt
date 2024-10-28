package io.element.android.support.zero.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.ZeroLogoSmall
import io.element.android.support.zero.common.ui.ZeroOnboardingViewBackground
import io.element.android.support.zero.common.ui.components.ElevatedStrip
import io.element.android.support.zero.common.ui.components.ZImageButton
import io.element.android.support.zero.common.ui.theme.PADDING_2X
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_4X

@Composable
fun ZeroOnboardingView(
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        ZeroOnboardingViewBackground {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(SPACING_10X.dp))
                Spacer(modifier = Modifier.size(SPACING_4X.dp))

                ZeroLogoSmall()

                Spacer(modifier = Modifier.size(SPACING_4X.dp))
                LoginStrip(onLogin = onSignIn)
            }
        }
    }
}

@Composable
private fun LoginStrip(onLogin: () -> Unit = {}) {
    ElevatedStrip(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp),
        strokePadding = 0.5.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = PADDING_4X.dp),
                painter = painterResource(id = R.drawable.ic_avatar),
                contentDescription = null
            )
            Box(modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(PADDING_2X.dp)) {
                ZImageButton(
                    image = R.drawable.img_btn_landing_login,
                    text = stringResource(id = R.string.log_in),
                    onClick = onLogin
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun ZeroOnboardingViewPreview() = ElementPreview {
    ZeroOnboardingView()
}
