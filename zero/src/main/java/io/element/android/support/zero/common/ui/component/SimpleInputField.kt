package io.element.android.support.zero.common.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.common.extension.innerShadow

private val TEXT_FIELD_BACKGROUND_SECONDARY = Color(0xBF262626)

@Composable
fun SimpleInputField(
	modifier: Modifier = Modifier,
	text: String,
	@StringRes placeholder: Int? = null,
	isFocused: Boolean = false,
	shape: Shape = RoundedCornerShape(8.dp),
	maxInputLength: Int = Int.MAX_VALUE,
	maxLines: Int = 1,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	sideEffectKey: Any? = Unit,
	textFieldColor: Color = TEXT_FIELD_BACKGROUND_SECONDARY,
	onFocusChanged: (Boolean) -> Unit = {},
	visualTransformation: VisualTransformation = VisualTransformation.None,
	trailingIcon: @Composable () -> Unit = {},
	onTextChanged: (String) -> Unit = {}
) {
	val focusRequester = remember { FocusRequester() }
	var textFieldValue: TextFieldValue by remember {
		mutableStateOf(TextFieldValue(text = text, selection = TextRange(text.length)))
	}

	Box(
		modifier =
		modifier
			.height(50.dp)
			.background(textFieldColor, shape)
			.innerShadow(
				color = Color(0x50F6F4F6),
				blur = 4.dp,
				spread = (-8).dp,
				offsetX = (-4).dp,
				offsetY = (-4).dp,
				cornersRadius = 8.dp
			)
	) {
		CustomTextFieldValue(
			value = textFieldValue,
			onValueChange = {
				if (it.text.length <= maxInputLength) {
					textFieldValue = it
					onTextChanged.invoke(it.text)
				}
			},
			placeholderText = placeholder?.let { stringResource(id = it) }.orEmpty(),
			textStyle =
			ElementTheme.zeroTypography
				.fontBodyLgRegular
				.copy(color = Color.White),
			placeHolderTextStyle =
            ElementTheme.zeroTypography
                .fontBodyLgRegular
				.copy(color = Color.Gray),
			modifier =
			Modifier.fillMaxSize().focusRequester(focusRequester).onFocusChanged { state ->
				onFocusChanged(state.isFocused)
			},
			textFieldColor = Color.Transparent,
			maxLines = maxLines,
			keyboardOptions = keyboardOptions,
			keyboardActions = keyboardActions,
			trailingIcon = trailingIcon,
			visualTransformation = visualTransformation
		)
	}

	LaunchedEffect(sideEffectKey) {
		if (isFocused) {
			focusRequester.requestFocus()
		}
	}
}

@Preview @Composable
fun SimpleInputFieldPreview() = SimpleInputField(text = "")
