package io.element.android.libraries.designsystem.theme.zero.color

import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors

internal val zeroAccentColor = Color(0xFF01F4CB)
internal val zeroDialogBackground = Color(0xFF1F1B22)
internal val zeroChatBubbleOutgoing = Color(0xFF400999)
internal val zeroChatBubbleIncoming = Color(0xFF111213)

internal val Color.Companion.ZeroNewBackground
    get() = Color(0xFF0D0D0D)

val SemanticColors.zeroBrandColor
    get() = zeroAccentColor

val SemanticColors.zeroBrandColorAlpha10
    get() = zeroAccentColor.copy(alpha = 0.10f)

val SemanticColors.zeroBrandColorAlpha15
    get() = zeroAccentColor.copy(alpha = 0.15f)

val SemanticColors.zeroBrandColorAlpha20
    get() = zeroAccentColor.copy(alpha = 0.20f)

val SemanticColors.zeroBrandColorAlpha50
    get() = zeroAccentColor.copy(alpha = 0.50f)

val SemanticColors.zeroDialogBackgroundColor
    get() = zeroDialogBackground

val SemanticColors.zeroChatBubbleOutgoingColor
    get() = zeroChatBubbleOutgoing

val SemanticColors.zeroChatBubbleIncomingColor
    get() = zeroChatBubbleIncoming
