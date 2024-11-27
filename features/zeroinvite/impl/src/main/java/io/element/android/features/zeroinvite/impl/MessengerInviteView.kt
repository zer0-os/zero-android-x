package io.element.android.features.zeroinvite.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite
import io.element.android.support.zero.common.APP_INSTALL_LINK
import io.element.android.support.zero.common.ui.component.SuccessTextBox
import io.element.android.support.zero.common.ui.component.ZImageButton
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_5X

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerInviteView(
    modifier: Modifier = Modifier,
    state: InviteState,
    onBackClick: () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val copyInvite: (String) -> Unit = { inviteCode ->
        val linkInfo = buildString {
            append(context.getString(R.string.here_is_invite_code))
            append("\n")
            append(inviteCode)
            append("\n\n")
            append(context.getString(R.string.download_the_app_here))
            append("\n")
            append(APP_INSTALL_LINK)
        }
        clipboardManager.setText(AnnotatedString(linkInfo.trim()))
    }

    //View from here
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                title = {}
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .consumeWindowInsets(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InviteHeader()
                InviteCodeContent(
                    messengerInvite = state.messengerInvite,
                    onCopyInviteCode = { slug ->
                        copyInvite.invoke(slug)
                    }
                )
            }
        }
    )
}

@Composable
fun ColumnScope.InviteHeader() {
    Image(
        painter = painterResource(R.drawable.img_messenger_invite),
        contentDescription = null
    )
    Spacer(modifier = Modifier.size(SPACING_5X.dp))
    Text(
        text = stringResource(R.string.invite_friend_earn_rewards),
        style = ElementTheme.zeroTypography.fontHeadingMdBold,
        color = ElementTheme.colors.textPrimary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.size(SPACING_10X.dp))
}

@Composable
fun ColumnScope.InviteCodeContent(
    messengerInvite: ZeroMessengerInvite,
    onCopyInviteCode: (String) -> Unit,
) {
    val remainingInvites = messengerInvite.remainingInvites
    if (remainingInvites > 0) {
        ZImageButton(
            image = R.drawable.img_btn_copy_invite,
            text = stringResource(id = R.string.copy_invite_code)
        ) {
            onCopyInviteCode(messengerInvite.slug)
        }
        Spacer(modifier = Modifier.size(SPACING_5X.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(remainingInvites.toString())
                }
                append(" ")
                append("Remaining")
            },
            style = ElementTheme.zeroTypography.fontBodyLgRegular,
            color = ElementTheme.colors.textPrimary,
        )
    } else {
        SuccessTextBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PADDING_4X.dp),
            text = stringResource(id = R.string.all_invites_consumed)
        )
    }
    Spacer(modifier = Modifier.size(SPACING_5X.dp))
}

@PreviewsDayNight
@Composable
fun MessengerInviteViewPreview() = ElementPreview {
    MessengerInviteView(state = InviteState(messengerInvite = ZeroMessengerInvite.empty()))
}
