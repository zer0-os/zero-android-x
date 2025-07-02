/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.common.MatrixSessionCommon
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX

@Immutable
data class HomeScreenChannel(
    val channelFullName: String
) {
    val displayTitle: String? = channelFullName
        .replace(ZERO_CHANNEL_PREFIX, "")
        .split(".")
        .firstOrNull()

    var notificationsCount = 0

    companion object {
        val placeHolder: HomeScreenChannel = HomeScreenChannel(
            "0://dummychannel.xyz"
        )
    }
}

fun HomeScreenChannel.channelId(): String? {
    return displayTitle?.let {
        "#${it}:${MatrixSessionCommon.getHomeServerPostfix()}"
    }
}
