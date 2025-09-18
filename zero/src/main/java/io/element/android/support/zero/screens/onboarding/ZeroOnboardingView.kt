package io.element.android.support.zero.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.StrikedLabel
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.theme.PADDING_6X
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPager
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.ZeroSecondaryButton

@Composable
fun ZeroOnboardingView(
    onSignIn: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onLoginWithX: () -> Unit = {},
    onLoginWithEpic: () -> Unit = {},
    onLoginWithWalletConnect: () -> Unit = {},
) {
//    LaunchedEffect(Unit) {
//        onSignIn.invoke()
//    }
//
//    Box(modifier = Modifier.fillMaxSize())
    ZeroOnboardingPage(
        modifier = Modifier.navigationBarsPadding(),
        appbarActions = {
            TextButton(
                text = "Sign up",
                onClick = onSignUp,
                contentColor = ElementTheme.colors.textPrimary
            )
        },
        content = {
            ZeroOnboardingPager(
                modifier = Modifier.padding(PADDING_6X.dp)
            )
        },
        footer = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_6X.dp)
            ) {
                ZeroPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Continue with",
                    icon = ImageVector.vectorResource(R.drawable.ic_logo_x),
                    onClick = onLoginWithX
                )
                StrikedLabel(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = "or via"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ZeroSecondaryButton(
                        modifier = Modifier.weight(1f),
                        text = "",
                        icon = ImageVector.vectorResource(R.drawable.ic_logo_email),
                        onClick = onSignIn
                    )
                    Spacer(Modifier.size(8.dp))
                    ZeroSecondaryButton(
                        modifier = Modifier.weight(1f),
                        text = "",
                        icon = ImageVector.vectorResource(R.drawable.ic_logo_walletconnect),
                        onClick = onLoginWithWalletConnect
                    )
                    Spacer(Modifier.size(8.dp))
                    ZeroSecondaryButton(
                        modifier = Modifier.weight(1f),
                        text = "",
                        icon = ImageVector.vectorResource(R.drawable.ic_logo_epic),
                        onClick = onLoginWithEpic
                    )
                }
            }
        }
    )
}

@PreviewsDayNight
@Composable
fun ZeroOnboardingViewPreview() = ElementPreview {
    ZeroOnboardingView()
}
