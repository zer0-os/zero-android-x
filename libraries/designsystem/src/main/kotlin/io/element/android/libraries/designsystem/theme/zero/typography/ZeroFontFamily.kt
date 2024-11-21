package io.element.android.libraries.designsystem.theme.zero.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.element.android.libraries.designsystem.R

internal object ZeroFontFamily {
    /** PRIMARY FONT */
    val InterFontFamily =
        FontFamily(
            Font(R.font.inter_regular, FontWeight.Normal),
            Font(R.font.inter_bold, FontWeight.Bold),
            Font(R.font.inter_extra_bold, FontWeight.ExtraBold),
            Font(R.font.inter_extra_light, FontWeight.ExtraLight),
            Font(R.font.inter_light, FontWeight.Light),
            Font(R.font.inter_medium, FontWeight.Medium),
            Font(R.font.inter_semi_bold, FontWeight.SemiBold),
            Font(R.font.inter_thin, FontWeight.Thin)
        )

    /** SECONDARY FONT */
    val RobotoMonoFontFamily =
        FontFamily(
            Font(R.font.roboto_mono_regular, FontWeight.Normal),
            Font(R.font.roboto_mono_bold, FontWeight.Bold),
            Font(R.font.roboto_mono_extra_light, FontWeight.ExtraLight),
            Font(R.font.roboto_mono_light, FontWeight.Light),
            Font(R.font.roboto_mono_medium, FontWeight.Medium),
            Font(R.font.roboto_mono_semi_bold, FontWeight.SemiBold),
            Font(R.font.roboto_mono_thin, FontWeight.Thin)
        )
}
