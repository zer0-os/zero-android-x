package io.element.android.support.zero.screens.confirmaccountprovider

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.common.ui.components.OverlappingLoadingContainer

@Composable
fun ZeroConfirmAccountProviderView(
    isLoading: Boolean = false,
    onInvoked: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    LaunchedEffect(Unit) { onInvoked.invoke() }

    OverlappingLoadingContainer(
        modifier = Modifier.fillMaxSize(),
        loading = isLoading,
        size = 64.dp
    ) {
        content()
    }
}

@PreviewsDayNight
@Composable
fun ZeroConfirmAccountProviderViewPreview() = ElementPreview {
    ZeroConfirmAccountProviderView()
}
