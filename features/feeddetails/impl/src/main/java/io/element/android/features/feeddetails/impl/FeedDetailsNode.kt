/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

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
import io.element.android.features.feeddetails.api.FeedDetailsEntryPoint
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@ContributesNode(SessionScope::class)
@Inject
class FeedDetailsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: FeedDetailsPresenter.Factory,
) : Node(buildContext, plugins = plugins) {

    private val inputs = inputs<FeedDetailsEntryPoint.Params>()
    private val presenter = presenterFactory.create(inputs.feed)
    private val callbacks = plugins.filterIsInstance<FeedDetailsEntryPoint.Callback>()

    private fun onFeedReplyClick(reply: ZeroFeed) {
        callbacks.forEach { it.onFeedReplyClick(reply) }
    }

    private fun onFeedUserClick(profile: FeedUserProfileView) {
        callbacks.forEach { it.onFeedUserClick(profile) }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val activity = LocalActivity.current as Activity

        FeedDetailsView(
            modifier = modifier,
            state = state,
            onBackClick = ::navigateUp,
            onFeedReplyClick = this::onFeedReplyClick,
            onFeedUserClick = this::onFeedUserClick
        )
    }
}
