/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

interface FeedUserProfileEntryPoint : FeatureEntryPoint {

    data class Params(
        val userId: UserId,
        val userProfile: FeedUserProfileView?,
    ) : NodeInputs

    interface Callback : Plugin {
        fun onUserFeedClick(feed: ZeroFeed)
        fun onOpenDm(roomId: RoomId)
        fun openAvatarPreview(name: String, url: String)
    }

    fun nodeBuilder(parentNode: Node, buildContext: BuildContext): NodeBuilder

    interface NodeBuilder {
        fun params(params: Params): NodeBuilder
        fun callback(callback: Callback): NodeBuilder
        fun build(): Node
    }
}
