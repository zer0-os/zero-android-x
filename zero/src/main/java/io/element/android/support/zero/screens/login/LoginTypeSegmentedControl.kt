package io.element.android.support.zero.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.common.ui.component.ElevatedButton

@Composable
fun LoginTypeSegmentedControl(
    modifier: Modifier = Modifier,
    controlWidth: Dp? = null,
    items: List<String>,
    defaultSelectedItemIndex: Int = 0,
    cornerRadius: Int = 75,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    val selectedIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }
    val itemIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }

    val segmentContainerColor = Color.White.copy(alpha = 0.05f)

    val mModifier =
        if (controlWidth == null) {
            modifier.fillMaxWidth()
        } else modifier.width(controlWidth)
    Row(
        modifier =
        mModifier
            .height(44.dp)
            .background(segmentContainerColor, RoundedCornerShape(cornerRadius)),
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { index, item ->
            itemIndex.intValue = index
            if (selectedIndex.intValue == index) {
                SegmentSelectedControl(controlWidth, text = item) {
                    selectedIndex.intValue = index
                    onItemSelection(selectedIndex.intValue)
                }
            } else {
                val itemModifier =
                    if (controlWidth == null) {
                        Modifier.weight(1f)
                    } else {
                        val itemWidth = controlWidth.div(2)
                        Modifier.width(itemWidth)
                    }
                Card(
                    modifier = itemModifier.fillMaxHeight(),
                    onClick = {
                        selectedIndex.intValue = index
                        onItemSelection(selectedIndex.intValue)
                    },
                    colors =
                    CardDefaults.cardColors(
                        containerColor =
                        if (selectedIndex.intValue == index) {
                            Color.White
                        } else {
                            Color.Transparent
                        },
                        contentColor = Color.White
                    ),
                    shape =
                    when (index) {
                        0 ->
                            RoundedCornerShape(
                                topStartPercent = cornerRadius,
                                topEndPercent = cornerRadius,
                                bottomStartPercent = cornerRadius,
                                bottomEndPercent = cornerRadius
                            )
                        items.size - 1 ->
                            RoundedCornerShape(
                                topStartPercent = cornerRadius,
                                topEndPercent = cornerRadius,
                                bottomStartPercent = cornerRadius,
                                bottomEndPercent = cornerRadius
                            )
                        else ->
                            RoundedCornerShape(
                                topStartPercent = 0,
                                topEndPercent = 0,
                                bottomStartPercent = 0,
                                bottomEndPercent = 0
                            )
                    }
                ) {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = item,
                            style = ElementTheme.typography.fontBodyLgRegular,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SegmentSelectedControl(
    controlWidth: Dp? = null,
    text: String,
    onClick: () -> Unit = {}
) {
    val mModifier =
        if (controlWidth == null) {
            Modifier.weight(1f)
        } else {
            val itemWidth = controlWidth.div(2)
            Modifier.width(itemWidth)
        }
    Box(modifier = mModifier) {
        ElevatedButton(
            modifier = mModifier.fillMaxHeight(),
            text = text,
            onClick = onClick,
            defaultContentColor = Color.White,
            elevation = 6.dp
        )
    }
}

@PreviewsDayNight
@Composable
fun LoginTypeSegmentedControlPreview() = ElementPreview {
    LoginTypeSegmentedControl(
        items = listOf("Item 1", "Item 2"),
        onItemSelection = {}
    )
}
