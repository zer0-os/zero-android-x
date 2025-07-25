package io.element.android.libraries.designsystem.theme.zero.typography

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.element.android.compound.tokens.generated.TypographyTokens

object ZeroTypographyTokens {
    val defaultHeadlineSmall = TextStyle(
        fontFamily = ZeroFontFamily.InterFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 32.sp,
        fontSize = 24.sp,
        letterSpacing = 0.em,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)
    )

    val fontBodyLgMedium = TypographyTokens.fontBodyLgMedium
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodyLgRegular = TypographyTokens.fontBodyLgRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodyLgRegularRoboto = TypographyTokens.fontBodyLgRegular
        .copy(fontFamily = ZeroFontFamily.RobotoMonoFontFamily)

    val fontBodyMdMedium = TypographyTokens.fontBodyMdMedium
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodyMdRegular = TypographyTokens.fontBodyMdRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodyMdRegularRoboto = TypographyTokens.fontBodyMdRegular
        .copy(fontFamily = ZeroFontFamily.RobotoMonoFontFamily)

    val fontBodySmMedium = TypographyTokens.fontBodySmMedium
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodySmRegular = TypographyTokens.fontBodySmRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodySmRegularRoboto = TypographyTokens.fontBodySmRegular
        .copy(fontFamily = ZeroFontFamily.RobotoMonoFontFamily)

    val fontBodyXsMedium = TypographyTokens.fontBodyXsMedium
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontBodyXsRegular = TypographyTokens.fontBodyXsRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingLgBold = TypographyTokens.fontHeadingLgBold
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingLgMediumRoboto = TypographyTokens.fontHeadingLgBold
        .copy(fontFamily = ZeroFontFamily.RobotoMonoFontFamily, fontWeight = FontWeight.Medium)

    val fontHeadingLgRegular = TypographyTokens.fontHeadingLgRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingMdBold = TypographyTokens.fontHeadingMdBold
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingMdRegular = TypographyTokens.fontHeadingMdRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingSmMedium = TypographyTokens.fontHeadingSmMedium
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingSmMediumRoboto = TypographyTokens.fontHeadingSmMedium
        .copy(fontFamily = ZeroFontFamily.RobotoMonoFontFamily)

    val fontHeadingSmRegular = TypographyTokens.fontHeadingSmRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingXlBold = TypographyTokens.fontHeadingXlBold
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    val fontHeadingXlRegular = TypographyTokens.fontHeadingXlRegular
        .copy(fontFamily = ZeroFontFamily.InterFontFamily)

    //Aliases
    val aliasScreenTitle = fontHeadingSmMedium
    val aliasButtonText = fontBodyLgMedium
}
