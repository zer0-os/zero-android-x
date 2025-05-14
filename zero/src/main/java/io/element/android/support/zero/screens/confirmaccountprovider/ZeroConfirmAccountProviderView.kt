package io.element.android.support.zero.screens.confirmaccountprovider

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha15
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.getActivity
import io.element.android.support.zero.common.ui.ZeroLogoSmall
import io.element.android.support.zero.common.ui.ZeroOnboardingViewBackground
import io.element.android.support.zero.common.ui.component.ElevatedStrip
import io.element.android.support.zero.common.ui.component.OverlappingLoadingContainer
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.component.ZImageButton
import io.element.android.support.zero.common.ui.component.keyboard.Keyboard
import io.element.android.support.zero.common.ui.component.keyboard.keyboardAsState
import io.element.android.support.zero.common.ui.theme.PADDING_10X
import io.element.android.support.zero.common.ui.theme.PADDING_2X
import io.element.android.support.zero.common.ui.theme.PADDING_3X
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import kotlin.math.roundToInt

@Composable
fun ZeroConfirmAccountProviderView(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onInvoked: () -> Unit = {},
    onValidateInviteCode: (String) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val keyboardState by keyboardAsState()

    val context = LocalContext.current

    val exitApp: () -> Unit = {
        context.getActivity()?.finishAffinity()
    }

    BackHandler { exitApp() }

    // for animation effect as per designs
    val pxToMove = with(LocalDensity.current) { 300.dp.toPx().roundToInt() }
    val offset by
    animateIntOffsetAsState(
        targetValue =
        if (keyboardState == Keyboard.Opened) {
            IntOffset(0, -pxToMove)
        } else {
            IntOffset.Zero
        },
        label = "offset"
    )

    OverlappingLoadingContainer(
        modifier = Modifier.fillMaxSize(),
        loading = isLoading
    ) {
        Box(modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            //.imePadding()
            .offset { offset }
        ) {
            ZeroOnboardingViewBackground {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(SPACING_10X.dp))
                    Spacer(modifier = Modifier.size(SPACING_4X.dp))

                    ZeroLogoSmall()

                    Spacer(modifier = Modifier.size(SPACING_4X.dp))
                    LoginStrip(onLogin = onInvoked)
                }
            }

            CreateAccountSection(
                onValidateInviteCode = onValidateInviteCode
            )
        }

        content()
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
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(PADDING_2X.dp)
            ) {
                ZImageButton(
                    image = R.drawable.img_btn_landing_login,
                    text = stringResource(id = R.string.log_in),
                    onClick = onLogin
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CreateAccountSection(
    onValidateInviteCode: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .align(Alignment.BottomCenter)
            .padding(vertical = PADDING_10X.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.create_account),
            style = ElementTheme.zeroTypography.fontBodyLgRegular,
            color = ElementTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.size(SPACING_4X.dp))

        InviteCodeTextField(
            onValidateInviteCode = onValidateInviteCode
        )

        Spacer(modifier = Modifier.size(SPACING_10X.dp))
    }
}

@Composable
private fun InviteCodeTextField(
    onValidateInviteCode: (String) -> Unit = {}
) {
    var inviteCodeFieldState by textFieldState(stateValue = "")

    Row(verticalAlignment = Alignment.CenterVertically) {
        SimpleInputField(
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = PADDING_3X.dp),
            text = inviteCodeFieldState,
            placeholder = R.string.invite_code,
            maxInputLength = 6,
            onTextChanged = {
                inviteCodeFieldState = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Characters
            ),
            keyboardActions = KeyboardActions(onDone = {
                onValidateInviteCode.invoke(inviteCodeFieldState)
            })
        )

        ConfirmButton(
            canSend = inviteCodeFieldState.isNotBlank(),
            onClick = {
                onValidateInviteCode.invoke(inviteCodeFieldState)
            }
        )
    }
}

@Composable
private fun ConfirmButton(
    canSend: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(48.dp),
        onClick = onClick,
        enabled = canSend,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(if (canSend)
                    ElementTheme.colors.zeroBrandColorAlpha15
                else
                    Color.Transparent
                )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = CompoundIcons.SendSolid(),
                contentDescription = null,
                tint = if (canSend) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.iconDisabled
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun ZeroConfirmAccountProviderViewPreview() = ElementPreview {
    ZeroConfirmAccountProviderView()
}
