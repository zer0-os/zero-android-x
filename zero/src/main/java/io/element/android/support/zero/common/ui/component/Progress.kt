package io.element.android.support.zero.common.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme

@Composable
fun CircularProgress(modifier: Modifier = Modifier, size: Dp = 42.dp, stroke: Dp = 5.dp) =
	CircularProgressIndicator(
		modifier = modifier.size(size),
		strokeWidth = stroke,
		color = ElementTheme.colors.textSecondary
	)

@Composable
fun LinearProgress(modifier: Modifier = Modifier) = LinearProgressIndicator(modifier = modifier)
