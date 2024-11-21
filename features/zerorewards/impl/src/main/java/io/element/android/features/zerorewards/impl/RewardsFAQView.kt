package io.element.android.features.zerorewards.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.zerorewards.impl.faqs.RewardScreenFAQs
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.ui.theme.PADDING_1X
import io.element.android.support.zero.common.ui.theme.PADDING_2X
import io.element.android.support.zero.common.ui.theme.PADDING_3X
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.PADDING_6X

@Composable
fun RewardsFAQView() {
    val context = LocalContext.current
    val faqs = RewardScreenFAQs.getFAQs()

    val selectedIndex = remember { mutableIntStateOf(-1) }
    val onFaqSelected: (Int) -> Unit = { index ->
        if (selectedIndex.intValue == index) {
            selectedIndex.intValue = -1
        } else selectedIndex.intValue = index
    }

    faqs.forEachIndexed { index, faq ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PADDING_6X.dp, vertical = PADDING_1X.dp)
        ) {
            val showAnswer = selectedIndex.intValue == index
            Box(modifier = Modifier.fillMaxWidth().clickable { onFaqSelected(index) }) {
                Row(
                    modifier = Modifier.padding(PADDING_2X.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        text = faq.question,
                        style = ElementTheme.zeroTypography.fontBodyLgRegular,
                        color = ElementTheme.colors.textPrimary
                    )
                    IconButton(onClick = { onFaqSelected(index) }) {
                        Icon(
                            imageVector =
                            if (!showAnswer) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Filled.KeyboardArrowUp
                            },
                            contentDescription = null,
                            tint = ElementTheme.colors.textPrimary
                        )
                    }
                }
            }
            if (showAnswer) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ClickableText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = PADDING_3X.dp, horizontal = PADDING_4X.dp),
                        text = faq.answer,
                        style = ElementTheme.zeroTypography.fontBodyLgRegular
                            .copy(color = ElementTheme.colors.textSecondary),
                        onClick = {
                            faq.answer.getStringAnnotations(it, it).firstOrNull()?.let { stringAnnotation ->
                                context.openExternalUri(stringAnnotation.item)
                            }
                        }
                    )
                }
            }
            Divider(
                thickness = 0.20.dp,
                color = ElementTheme.colors.textSecondary.copy(0.5f)
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun RewardsFAQViewPreview() = ElementPreview {
    RewardsFAQView()
}
