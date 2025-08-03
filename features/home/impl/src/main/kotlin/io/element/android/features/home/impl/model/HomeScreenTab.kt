/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.annotation.DrawableRes
import io.element.android.support.zero.R

enum class HomeScreenTab(
    val title: String,
    @DrawableRes val icon: Int
) {
    CHAT("Chat", R.drawable.home_tab_chat_icon),
    CHANNEL("Channels", R.drawable.home_tab_channel_icon),
    FEED("Feed", R.drawable.home_tab_feed_icon),
    NOTIFICATION("Notifications", R.drawable.home_tab_notification_icon),
    WALLET("Wallet", R.drawable.home_tab_wallet_icon),
//    PROFILE("Profile", R.drawable.home_tab_profile_icon),
}
