/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.Inject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.features.roommembermoderation.api.ModerationAction
import io.element.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.element.android.features.roommembermoderation.api.RoomMemberModerationRenderer
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.primaryZIdOrWalletAddress
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ContributesNode(RoomScope::class)
@Inject
class RoomMemberListNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: RoomMemberListPresenter,
    private val analyticsService: AnalyticsService,
    private val roomMemberModerationRenderer: RoomMemberModerationRenderer,
) : Node(buildContext, plugins = plugins), RoomMemberListNavigator {
    interface Callback : Plugin {
        fun openRoomMemberDetails(roomMemberId: UserId, primaryZId: String?)
        fun openInviteMembers()
    }

    private val callbacks = plugins<Callback>()

    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomMembers))
            }
        )
    }

    override fun openRoomMemberDetails(roomMemberId: UserId, primaryZId: String?) {
        callbacks.forEach {
            it.openRoomMemberDetails(roomMemberId, primaryZId)
        }
    }

    override fun openInviteMembers() {
        callbacks.forEach {
            it.openInviteMembers()
        }
    }

    override fun exitRoomMemberList() {
        navigateUp()
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RoomMemberListView(
            state = state,
            modifier = modifier,
            navigator = this,
        )
        roomMemberModerationRenderer.Render(
            state = state.moderationState,
            onSelectAction = { action, target ->
                when (action) {
                    is ModerationAction.DisplayProfile -> lifecycleScope.getUserInfo(target)
                    else -> state.moderationState.eventSink(RoomMemberModerationEvents.ProcessAction(action, target))
                }
            },
            modifier = Modifier,
        )
    }

    private fun CoroutineScope.getUserInfo(user: MatrixUser) = launch {
        presenter.room.getUpdatedMember(user.userId)
            .onSuccess { member ->
                openRoomMemberDetails(member.userId, member.primaryZId)
            }
            .onFailure {
                openRoomMemberDetails(user.userId, null)
            }
    }
}

interface RoomMemberListNavigator {
    fun exitRoomMemberList() {}
    fun openRoomMemberDetails(roomMemberId: UserId, primaryZId: String?) {}
    fun openInviteMembers() {}
}
