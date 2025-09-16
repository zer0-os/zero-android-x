package io.element.android.features.zeroinvite.impl

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.StrikedLabel
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite
import io.element.android.support.zero.common.APP_INSTALL_LINK
import io.element.android.support.zero.common.ui.component.SuccessTextBox
import io.element.android.support.zero.common.ui.theme.SPACING_1X
import io.element.android.support.zero.common.ui.theme.SPACING_3X

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
            append(" ")
            append(inviteCode)
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
                    .consumeWindowInsets(it)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InviteHeader()
                InviteFriendBenefits()
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
        modifier = Modifier.size(200.dp),
        painter = painterResource(R.drawable.img_messenger_invite),
        contentDescription = null
    )
    Spacer(modifier = Modifier.size(SPACING_1X.dp))
    Text(
        text = "Refer a Friend",
        style = ElementTheme.zeroTypography.fontHeadingLgBold,
        color = ElementTheme.colors.zeroBrandColor,
        textAlign = TextAlign.Center
    )
    Text(
        text = "Earn 30% of pro subs from your code.",
        style = ElementTheme.zeroTypography.fontBodyLgRegular,
        color = ElementTheme.colors.textPrimary,
        textAlign = TextAlign.Center
    )
}

@Composable
fun InviteFriendBenefits() {
    Spacer(modifier = Modifier.size(SPACING_3X.dp))

    InviteFriendBenefitBox(
        title = "Receive a Bonus",
        description = "Both you and the invitee get free tokens"
    )
    InviteFriendBenefitBox(
        title = "Earn Passive Income",
        description = "Receive 30% of Pro subscription revenue"
    )
    InviteFriendBenefitBox(
        title = "Grow your Reputation",
        description = "Increase your clout with a bigger network"
    )
}

@Composable
fun InviteFriendBenefitBox(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .background(ElementTheme.colors.bgCanvasDefaultLevel1, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = ElementTheme.zeroTypography.fontBodyLgMedium,
                color = ElementTheme.colors.textPrimary,
            )
            Text(
                text = description,
                style = ElementTheme.zeroTypography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
            )
        }

        Icon(
            imageVector = CompoundIcons.Check(),
            contentDescription = null,
            tint = ElementTheme.colors.zeroBrandColor
        )
    }
}

@Composable
fun ColumnScope.InviteCodeContent(
    messengerInvite: ZeroMessengerInvite,
    onCopyInviteCode: (String) -> Unit,
) {
    val remainingInvites = messengerInvite.remainingInvites
    if (remainingInvites > 0) {
        StrikedLabel(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
            text = "Your referral code"
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 6.dp),
        ) {
            InviteInfoBox(
                modifier = Modifier.weight(1f),
                title = "Total invited so far",
                value = messengerInvite.invitesUsed.toString()
            )
            Spacer(Modifier.size(12.dp))
            InviteInfoBox(
                modifier = Modifier.weight(1f),
                title = "Pro subs",
                value = messengerInvite.proSubscriptionsCount.toString()
            )
        }

        ReferralCodeStrip(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            referralCode = messengerInvite.slug,
            onCopyCode = { onCopyInviteCode(messengerInvite.slug) }
        )

        ShareReferralCodeButton(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            onTap = {
                onCopyInviteCode(messengerInvite.slug)
            }
        )
    } else {
        SuccessTextBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            text = stringResource(id = R.string.all_invites_consumed)
        )
    }
}

@Composable
fun InviteInfoBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    Column(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = ElementTheme.colors.textSecondary,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = ElementTheme.zeroTypography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary,
        )
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = value,
            style = ElementTheme.zeroTypography.fontHeadingLgRegular,
            color = ElementTheme.colors.zeroBrandColor,
        )
    }
}

@Composable
fun ReferralCodeStrip(
    modifier: Modifier = Modifier,
    referralCode: String,
    onCopyCode: () -> Unit = {}
) {
    val isInviteCopied = remember { mutableStateOf(false) }

    val copyInvite: () -> Unit = {
        if (!isInviteCopied.value) {
            onCopyCode.invoke()
            isInviteCopied.value = true
            Handler(Looper.getMainLooper()).postDelayed({
                isInviteCopied.value = false
            }, 2000)
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { copyInvite() }
            .padding(vertical = 4.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { copyInvite() },
            text = if (isInviteCopied.value) {
                "Text Copied!"
            } else {
                referralCode
            },
            style = ElementTheme.zeroTypography.fontHeadingSmRegular,
            color = ElementTheme.colors.zeroBrandColor,
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { copyInvite() }
        ) {
            Icon(
                imageVector = if (isInviteCopied.value) {
                    Icons.Default.Check
                } else {
                    Icons.Default.ContentCopy
                },
                contentDescription = null,
                tint = ElementTheme.colors.zeroBrandColor
            )
        }
    }
}

@Composable
fun ShareReferralCodeButton(
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
            "Share Invite",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@PreviewsDayNight
@Composable
fun MessengerInviteViewPreview() = ElementPreview {
    MessengerInviteView(state = InviteState(messengerInvite = ZeroMessengerInvite.empty()))
}
