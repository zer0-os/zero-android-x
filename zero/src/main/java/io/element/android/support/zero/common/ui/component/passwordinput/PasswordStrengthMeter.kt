package io.element.android.support.zero.common.ui.component.passwordinput

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R
import io.element.android.support.zero.common.util.ValidationUtil

private val EmeraldGreen = Color(0xFF58C573)
private val Red300 = Color(0xFFFF6369)
private val Blue300 = Color(0xFF52CBFF)

private enum class StrengthLevel(@StringRes val textId: Int, val color: Color) {
	WEAK(R.string.weak, Red300),
	ACCEPTABLE(R.string.acceptable, Blue300),
	STRONG(R.string.strong, EmeraldGreen)
}

private interface CalculatorInterface {
	fun calculatePasswordLevel(password: String): StrengthLevel
}

private object PasswordCalculator : CalculatorInterface {
	override fun calculatePasswordLevel(password: String): StrengthLevel {
		return if (ValidationUtil.validatePassword(password) == null) {
			if (password.length >= 14) StrengthLevel.STRONG else StrengthLevel.ACCEPTABLE
		} else StrengthLevel.WEAK
	}
}

@Composable
fun PasswordStrengthMeter(modifier: Modifier, password: String = "") {
	val defaultBarColor = Color.White.copy(0.1f)
	val strengthLevel = PasswordCalculator.calculatePasswordLevel(password)
	val secondBarColor =
		when (strengthLevel) {
			StrengthLevel.WEAK -> defaultBarColor
			else -> strengthLevel.color
		}
	val thirdBarColor =
		when (strengthLevel) {
			StrengthLevel.STRONG -> strengthLevel.color
			else -> defaultBarColor
		}

	Column(modifier = modifier) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Box(
				modifier =
				Modifier.weight(1f)
					.height(4.dp)
					.background(strengthLevel.color, RoundedCornerShape(12.dp))
			)
			Spacer(modifier = Modifier.size(8.dp))
			Box(
				modifier =
				Modifier.weight(1f)
					.height(4.dp)
					.background(secondBarColor, RoundedCornerShape(12.dp))
			)
			Spacer(modifier = Modifier.size(8.dp))
			Box(
				modifier =
				Modifier.weight(1f)
					.height(4.dp)
					.background(thirdBarColor, RoundedCornerShape(12.dp))
			)
		}
		Text(
			modifier = Modifier.padding(vertical = 4.dp),
			text =
			buildAnnotatedString {
				withStyle(SpanStyle(color = Color.White)) {
					append(stringResource(R.string.password_strength))
				}
				append(" ")
				withStyle(SpanStyle(color = strengthLevel.color)) {
					append(stringResource(strengthLevel.textId))
				}
			},
			style = MaterialTheme.typography.bodySmall
		)
		if (strengthLevel == StrengthLevel.WEAK) {
			Text(
				modifier = Modifier.padding(vertical = 4.dp),
				text = stringResource(R.string.password_validation),
				style = MaterialTheme.typography.bodySmall,
				color = strengthLevel.color
			)
		}
	}
}

@PreviewsDayNight
@Composable
fun PasswordStrengthMeterPreview() = ElementTheme {
    PasswordStrengthMeter(Modifier)
}
