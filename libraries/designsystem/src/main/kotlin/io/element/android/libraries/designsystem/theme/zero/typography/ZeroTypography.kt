package io.element.android.libraries.designsystem.theme.zero.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import io.element.android.compound.theme.ElementTheme

internal val zeroTypography = Typography(
    // displayLarge = , 57px (Material) size. We have no equivalent
    // displayMedium = , 45px (Material) size. We have no equivalent
    // displaySmall = , 36px (Material) size. We have no equivalent
    headlineLarge = ZeroTypographyTokens.fontHeadingXlRegular,
    headlineMedium = ZeroTypographyTokens.fontHeadingLgRegular,
    headlineSmall = ZeroTypographyTokens.defaultHeadlineSmall,
    titleLarge = ZeroTypographyTokens.fontHeadingMdRegular,
    titleMedium = ZeroTypographyTokens.fontBodyLgMedium,
    titleSmall = ZeroTypographyTokens.fontBodyMdMedium,
    bodyLarge = ZeroTypographyTokens.fontBodyLgRegular,
    bodyMedium = ZeroTypographyTokens.fontBodyMdRegular,
    bodySmall = ZeroTypographyTokens.fontBodySmRegular,
    labelLarge = ZeroTypographyTokens.fontBodyMdMedium,
    labelMedium = ZeroTypographyTokens.fontBodySmMedium,
    labelSmall = ZeroTypographyTokens.fontBodyXsMedium,
)

val ElementTheme.zeroTypography: ZeroTypographyTokens
    get() = ZeroTypographyTokens
