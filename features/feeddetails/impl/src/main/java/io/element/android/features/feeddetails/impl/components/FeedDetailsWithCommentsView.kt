/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl.components

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.feeddetails.impl.FeedDetailsEvents
import io.element.android.features.feeddetails.impl.FeedDetailsState
import io.element.android.features.roomlist.impl.components.HomeFeedRow
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha15
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.extension.innerShadow
import io.element.android.support.zero.common.ui.component.feed.FeedMediaImageView
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedDetailsWithCommentsView(
    modifier: Modifier = Modifier,
    state: FeedDetailsState,
    onReplyClick: (ZeroFeed) -> Unit,
    onAddMeowToFeed: (ZeroFeed, Int) -> Unit,
) {
    var refreshing by remember(state) { mutableStateOf(false) }
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        refreshing = true
        state.eventSink(FeedDetailsEvents.RefreshFeed)
        Handler(Looper.getMainLooper()).postDelayed({
            refreshing = false
        }, 1_500)
    })
    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.feedComments.lastIndex + 1
        }
    }

    // Load more comments when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems) {
            isLoadingMoreItems = true
            state.eventSink(FeedDetailsEvents.LoadMoreReplies)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column {
                    FeedDetailsCell(
                        feed = state.zeroFeed,
                        zeroUserRewards = state.userRewards,
                        isMyOwnFeed = state.zeroFeed.userId == state.loggedInUserId,
                        onAddMeowToFeed = { meowCount ->
                            onAddMeowToFeed(state.zeroFeed, meowCount)
                        }
                    )
                    HorizontalDivider()
                }
            }

            itemsIndexed(
                items = state.feedComments
            ) { index, comment ->
                val nextComment = state.feedComments.getOrNull(index + 1)
                val showThreadLine = nextComment?.userId == comment.userId
                val media = state.feedCommentsMediaMap[comment.id]
                val linkMetaData = state.feedCommentsLinkMetaDataMap[comment.id]

                HomeFeedRow(
                    feed = comment.copy(media = media, linkMetaData = linkMetaData),
                    zeroUserRewards = state.userRewards,
                    isMyOwnFeed = comment.userId == state.loggedInUserId,
                    showThreadLine = showThreadLine,
                    onFeedClick = { onReplyClick(
                        comment.copy(media = media, linkMetaData = linkMetaData)
                    ) },
                    onAddMeowToFeed = { meowCount ->
                        onAddMeowToFeed(comment, meowCount)
                    }
                )
                if (index != state.feedComments.lastIndex) {
                    HorizontalDivider()
                }
            }

            item {
                if (state.postReplyAttachment != null) {
                    Spacer(modifier = Modifier.size(140.dp))
                } else {
                    Spacer(modifier = Modifier.size(60.dp))
                }
            }
        }

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))

        PostReplyView(state = state)

        if (state.genericActionState is AsyncData.Loading) {
            ProgressDialog(text = "Posting...")
        }

        if (state.genericActionState is AsyncData.Failure) {
            ErrorDialog(
                content = state.genericActionState.error.message ?: stringResource(CommonStrings.error_unknown),
                onSubmit = { state.eventSink(FeedDetailsEvents.HideError) }
            )
        }
    }
}

@Composable
private fun BoxScope.PostReplyView(
    state: FeedDetailsState,
    modifier: Modifier = Modifier,
) {
    val hasPostReplyAttachment = state.postReplyAttachment != null
    val columnBg = if (hasPostReplyAttachment) Color.Black else Color.Transparent

    Column(modifier = modifier
        .background(columnBg)
        .align(Alignment.BottomCenter)
        .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        if (hasPostReplyAttachment) {
            Row(modifier = Modifier.fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
                PostReplyAttachmentView(
                    media = state.postReplyAttachment!!.media,
                    onRemoveMedia = {
                        state.eventSink(FeedDetailsEvents.RemoveMedia)
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                avatarData = state.matrixUser.getAvatarData(size = AvatarSize.UserListItem),
            )
            PostReplyTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                text = state.postReplyText,
                onTextChanged = { text ->
                    state.eventSink(FeedDetailsEvents.PostReplyTextChanged(text))
                }
            )
            PostReplyAttachmentButton {
                state.eventSink(FeedDetailsEvents.SelectMedia)
            }
            PostReplySendButton(
                canPostReply = state.canPostReply,
                onPostReply = {
                    state.eventSink(FeedDetailsEvents.PostReply)
                }
            )
        }
    }
}

@Composable
fun PostReplyAttachmentView(
    media: File,
    onRemoveMedia: () -> Unit,
) {
    Box {
        FeedMediaImageView(
            file = media,
            modifier = Modifier
                .size(75.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.Center)
                .padding(12.dp)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.DarkGray, CircleShape)
                .size(24.dp),
            onClick = onRemoveMedia,
        ) {
            Icon(modifier = Modifier.fillMaxSize(),
                imageVector = CompoundIcons.Close(),
                contentDescription = null)
        }
    }
}

@Composable
private fun PostReplyTextField(
    modifier: Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    Box(
        modifier = modifier
            .background(Color(0xBF262626), RoundedCornerShape(24.dp))
            .innerShadow(
                color = Color(0x50F6F4F6),
                blur = 4.dp,
                spread = (-8).dp,
                offsetX = (-4).dp,
                offsetY = (-4).dp,
                cornersRadius = 24.dp
            )
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = { Text("Post your reply") },
            singleLine = false,
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun PostReplyAttachmentButton(
    onSelectAttachment: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = onSelectAttachment
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(ElementTheme.colors.zeroBrandColorAlpha15)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = CompoundIcons.Attachment(),
                contentDescription = null,
                tint = ElementTheme.colors.zeroBrandColor
            )
        }
    }
}

@Composable
private fun PostReplySendButton(
    canPostReply: Boolean = false,
    onPostReply: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = onPostReply,
        enabled = canPostReply,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                    if (canPostReply)
                        ElementTheme.colors.zeroBrandColorAlpha15
                    else
                        Color.Transparent
                )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = CompoundIcons.SendSolid(),
                contentDescription = null,
                tint = if (canPostReply) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.iconDisabled
            )
        }
    }
}
