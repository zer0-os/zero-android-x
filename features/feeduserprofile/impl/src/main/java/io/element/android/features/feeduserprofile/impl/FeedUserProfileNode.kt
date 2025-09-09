/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.Inject
import io.element.android.annotations.ContributesNode
import io.element.android.features.feeduserprofile.api.FeedUserProfileEntryPoint
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@ContributesNode(SessionScope::class)
@Inject
class FeedUserProfileNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: FeedUserProfilePresenter.Factory,
) : Node(buildContext, plugins = plugins) {

    private val inputs = inputs<FeedUserProfileEntryPoint.Params>()
    private val presenter = presenterFactory.create(inputs.userId, inputs.userProfile)
    private val callbacks = plugins.filterIsInstance<FeedUserProfileEntryPoint.Callback>()

    private fun onUserFeedClick(feed: ZeroFeed) {
        callbacks.forEach { it.onUserFeedClick(feed) }
    }

    private fun onOpenDm(roomId: RoomId) {
        callbacks.forEach { it.onOpenDm(roomId) }
    }

    private fun openAvatarPreview(name: String, url: String) {
        callbacks.forEach { it.openAvatarPreview(name, url) }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val activity = LocalActivity.current as Activity

        FeedUserProfileView(
            modifier = modifier,
            state = state,
            onBackClick = ::navigateUp,
            onOpenDm = ::onOpenDm,
            openAvatarPreview = ::openAvatarPreview,
            onUserFeedClick = this::onUserFeedClick
        )
    }
}
