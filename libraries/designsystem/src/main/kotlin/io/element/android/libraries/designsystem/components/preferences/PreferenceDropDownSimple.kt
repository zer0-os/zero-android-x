/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package io.element.android.libraries.designsystem.components.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.designsystem.toEnabledColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

interface SimpleDropdownOption {
    val text: String
}

@Composable
fun <T : SimpleDropdownOption> PreferenceDropDownSimple(
    modifier: Modifier = Modifier,
    title: String?,
    selectedOption: T?,
    options: ImmutableList<T>,
    enabled: Boolean = true,
    onOptionSelected: (T) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        title?.let {
            Text(
                style = ElementTheme.zeroTypography.fontBodyMdRegular,
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = enabled.toEnabledColor(),
            )
            Spacer(Modifier.size(8.dp))
        }

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it }
        ) {
            TextField(
                value = selectedOption?.text.orEmpty(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled),
                trailingIcon = {
                    if (isDropdownExpanded) {
                        Icon(Icons.Default.ArrowDropUp, contentDescription = "DropUp Icon")
                    } else {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                    }
                }
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.text) },
                        onClick = {
                            onOptionSelected(option)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(group = PreviewGroup.Preferences)
@Composable
internal fun PreferenceDropdownSimplePreview() = ElementThemedPreview {
    val options = listOf(
        object : SimpleDropdownOption {
            override val text = "Option 1"
        },
        object : SimpleDropdownOption {
            override val text = "Option 2"
        },
        object : SimpleDropdownOption {
            override val text = "Option 3"
        },
    ).toImmutableList()

    PreferenceDropDownSimple(
        title = "Dropdown",
        selectedOption = options.first(),
        options = options,
        onOptionSelected = {},
    )
}
