/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.messages.impl.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.core.text.getSpans
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.common.MatrixSessionCommon
import io.element.android.libraries.matrix.api.core.MatrixPatternType
import io.element.android.libraries.matrix.api.core.MatrixPatterns
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkBuilder
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.ui.messages.RoomMemberProfilesCache
import io.element.android.libraries.textcomposer.mentions.MentionSpan
import io.element.android.libraries.textcomposer.mentions.MentionSpanProvider
import io.element.android.support.zero.common.util.ZeroMentionPatterns
import javax.inject.Inject

interface TextPillificationHelper {
    fun pillify(text: CharSequence): CharSequence
    fun pillifyWithZero(text: CharSequence): CharSequence
}

@ContributesBinding(RoomScope::class)
class DefaultTextPillificationHelper @Inject constructor(
    private val mentionSpanProvider: MentionSpanProvider,
    private val permalinkBuilder: PermalinkBuilder,
    private val permalinkParser: PermalinkParser,
    private val roomMemberProfilesCache: RoomMemberProfilesCache,
) : TextPillificationHelper {
    @Suppress("LoopWithTooManyJumpStatements")
    override fun pillify(text: CharSequence): CharSequence {
        val matches = MatrixPatterns.findPatterns(text, permalinkParser).sortedByDescending { it.end }
        if (matches.isEmpty()) return text

        val spannable = SpannableStringBuilder(text)
        for (match in matches) {
            when (match.type) {
                MatrixPatternType.USER_ID -> {
                    val mentionSpanExists = spannable.getSpans<MentionSpan>(match.start, match.end).isNotEmpty()
                    if (!mentionSpanExists) {
                        val userId = UserId(match.value)
                        val permalink = permalinkBuilder.permalinkForUser(userId).getOrNull() ?: continue
                        val mentionSpan = mentionSpanProvider.getMentionSpanFor(match.value, permalink)
                        roomMemberProfilesCache.getDisplayName(userId)?.let { mentionSpan.text = it }
                        spannable.replace(match.start, match.end, "@ ")
                        spannable.setSpan(mentionSpan, match.start, match.start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                MatrixPatternType.ROOM_ALIAS -> {
                    val mentionSpanExists = spannable.getSpans<MentionSpan>(match.start, match.end).isNotEmpty()
                    if (!mentionSpanExists) {
                        val permalink = permalinkBuilder.permalinkForRoomAlias(RoomAlias(match.value)).getOrNull() ?: continue
                        val mentionSpan = mentionSpanProvider.getMentionSpanFor(match.value, permalink)
                        spannable.replace(match.start, match.end, "@ ")
                        spannable.setSpan(mentionSpan, match.start, match.start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                MatrixPatternType.AT_ROOM -> {
                    val mentionSpanExists = spannable.getSpans<MentionSpan>(match.start, match.end).isNotEmpty()
                    if (!mentionSpanExists) {
                        val mentionSpan = mentionSpanProvider.getMentionSpanFor("@room", "")
                        spannable.replace(match.start, match.end, "@ ")
                        spannable.setSpan(mentionSpan, match.start, match.start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                else -> Unit
            }
        }
        return spannable
    }

    override fun pillifyWithZero(text: CharSequence): CharSequence {
        val matches = ZeroMentionPatterns.ZERO_USER_MENTION_REGEX.findAll(text)
        if (matches.none()) return text

        val spannable = SpannableStringBuilder(text)
        for (match in matches) {
            val start = match.range.first
            val end = match.range.last

            val mentionSpanExists = spannable.getSpans<MentionSpan>(start, end + 1).isNotEmpty()
            if (!mentionSpanExists) {
                val matrixUserId = "@${match.groupValues[2]}:${MatrixSessionCommon.getHomeServerPostfix()}"
                val userId = UserId(matrixUserId)
                val permalink = permalinkBuilder.permalinkForUser(userId).getOrNull() ?: continue
                val mentionSpan = mentionSpanProvider.getMentionSpanFor(match.value, permalink)
                roomMemberProfilesCache.getDisplayName(userId)?.let { mentionSpan.text = it }
                match.groupValues[1].let { mentionSpan.text = it }
                spannable.replace(start, end + 1, "@")
                spannable.setSpan(mentionSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }
}
