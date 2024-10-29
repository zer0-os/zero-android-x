package io.element.android.support.zero.common.ui.components.passwordinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.components.ErrorTextBox
import io.element.android.support.zero.common.ui.components.InfoBox
import io.element.android.support.zero.common.ui.components.SimpleInputField
import io.element.android.support.zero.common.ui.components.SuccessTextBox
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.utils.ValidationUtil

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    placeHolder: Int? = null,
    showPasswordCriteria: Boolean = false,
    showPasswordStrengthMeter: Boolean = false,
    error: Int? = null,
    onTextChanged: (String) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    iconTint: Color = ElementTheme.colors.bgAccentHovered,
    imeAction: ImeAction = ImeAction.Done,
    onKeyboardActionDone: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showTrailingIcon by remember { mutableStateOf(false) }

    var showPasswordCriteriaInternal by remember { mutableStateOf(false) }
    var checkPasswordCriteriaInternal by remember { mutableIntStateOf(0) }
    var isPasswordCriteriaMet: Boolean? by remember { mutableStateOf(null) }

    val checkPasswordCriteria: (String) -> Unit = {
        val passwordError = ValidationUtil.validatePassword(it)
        isPasswordCriteriaMet = passwordError == null
    }

    Column {
        SimpleInputField(
            modifier = modifier,
            text = password,
            placeholder = placeHolder,
            onTextChanged = {
                password = it
                checkPasswordCriteriaInternal = 2
                checkPasswordCriteria(password)
                onTextChanged.invoke(it)
            },
            onFocusChanged = {
                if (!it) {
                    passwordVisible = false
                } else {
                    showPasswordCriteriaInternal = true
                }
                checkPasswordCriteriaInternal += 1
                showTrailingIcon = it
                onFocusChanged.invoke(it)
            },
            visualTransformation =
            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff

                if (showTrailingIcon) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, null, tint = iconTint)
                    }
                }
            },
            keyboardActions = KeyboardActions(onDone = { onKeyboardActionDone.invoke() })
        )
        if (showPasswordCriteria && showPasswordCriteriaInternal && error == null) {
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            when {
                isPasswordCriteriaMet == true -> {
                    SuccessTextBox(text = stringResource(id = R.string.password_criteria))
                }
                isPasswordCriteriaMet == false && checkPasswordCriteriaInternal >= 3 -> {
                    ErrorTextBox(text = stringResource(id = R.string.password_criteria))
                }
                else -> InfoBox(text = stringResource(id = R.string.password_criteria))
            }
        }
        if (showPasswordStrengthMeter && showTrailingIcon) {
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            PasswordStrengthMeter(modifier = Modifier.fillMaxWidth(), password = password)
        }
    }
}

@PreviewsDayNight
@Composable
fun PasswordTextFieldPreview() = ElementTheme {
    PasswordTextField(
        placeHolder = R.string.password,
        showPasswordCriteria = true,
        showPasswordStrengthMeter = true
    )
}
