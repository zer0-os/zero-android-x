/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api

import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.core.MatrixPatterns
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.createroom.CreateRoomParameters
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.media.MatrixMediaLoader
import io.element.android.libraries.matrix.api.media.MediaPreviewService
import io.element.android.libraries.matrix.api.notification.NotificationService
import io.element.android.libraries.matrix.api.notificationsettings.NotificationSettingsService
import io.element.android.libraries.matrix.api.oidc.AccountManagementAction
import io.element.android.libraries.matrix.api.pusher.PushersService
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.NotJoinedRoom
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.room.RoomMembershipObserver
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias
import io.element.android.libraries.matrix.api.roomdirectory.RoomDirectoryService
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.api.sync.SlidingSyncVersion
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.user.MatrixSearchUserResults
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import io.element.android.libraries.matrix.api.zero.feed.CreateFeedMediaAttachment
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingConfig
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingStatus
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUserRewardsInfo
import io.element.android.libraries.matrix.api.zero.staking.ZeroTokenAddress
import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenBalance
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.Optional

interface MatrixClient {
    val sessionId: SessionId
    val deviceId: DeviceId
    val userProfile: StateFlow<MatrixUser>
    val roomListService: RoomListService
    val mediaLoader: MatrixMediaLoader
    val sessionCoroutineScope: CoroutineScope
    val ignoredUsersFlow: StateFlow<ImmutableList<UserId>>

    val shouldShowNewRewardsIntimation: StateFlow<Boolean>
    val userRewards: StateFlow<ZeroUserRewards>
    val userZIds: StateFlow<List<String>>

    val allFeeds: StateFlow<List<ZeroFeed>>
    val allMyFeeds: StateFlow<List<ZeroFeed>>

    suspend fun getJoinedRoom(roomId: RoomId): JoinedRoom?
    suspend fun getRoom(roomId: RoomId): BaseRoom?
    suspend fun findDM(userId: UserId): Result<RoomId?>
    suspend fun getJoinedRoomIds(): Result<Set<RoomId>>
    suspend fun ignoreUser(userId: UserId): Result<Unit>
    suspend fun unignoreUser(userId: UserId): Result<Unit>
    suspend fun createRoom(createRoomParams: CreateRoomParameters): Result<RoomId>
    suspend fun createDM(userId: UserId): Result<RoomId>
    suspend fun getProfile(userId: UserId): Result<MatrixUser>
    suspend fun getZeroUsers(userIds: List<String>): Result<List<ZeroUser>>
    suspend fun searchUsers(searchTerm: String, limit: Long): Result<MatrixSearchUserResults>
    suspend fun setDisplayName(displayName: String): Result<Unit>
    suspend fun setDisplayNameOrZid(displayName: String, primaryZId: String): Result<Unit>
    suspend fun uploadAvatar(mimeType: String, data: ByteArray): Result<Unit>
    suspend fun removeAvatar(): Result<Unit>
    suspend fun joinRoom(roomId: RoomId): Result<RoomInfo?>
    suspend fun leaveInvitedRoom(roomId: RoomId): Result<Unit>
    suspend fun joinRoomByIdOrAlias(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>): Result<RoomInfo?>
    suspend fun knockRoom(roomIdOrAlias: RoomIdOrAlias, message: String, serverNames: List<String>): Result<RoomInfo?>
    fun syncService(): SyncService
    fun sessionVerificationService(): SessionVerificationService
    fun pushersService(): PushersService
    fun notificationService(): NotificationService
    fun notificationSettingsService(): NotificationSettingsService
    fun encryptionService(): EncryptionService
    fun roomDirectoryService(): RoomDirectoryService
    fun mediaPreviewService(): MediaPreviewService
    suspend fun getCacheSize(): Long

    /**
     * Will close the client and delete the cache data.
     */
    suspend fun clearCache()

    /**
     * Logout the user.
     *
     * @param userInitiated if false, the logout came from the HS, no request will be made and the session entry will be kept in the store.
     * @param ignoreSdkError if true, the SDK will ignore any error and delete the session data anyway.
     */
    suspend fun logout(userInitiated: Boolean, ignoreSdkError: Boolean)

    /**
     * Retrieve the user profile, will also eventually emit a new value to [userProfile].
     */
    suspend fun getUserProfile(): Result<MatrixUser>
    suspend fun getAccountManagementUrl(action: AccountManagementAction?): Result<String?>
    suspend fun uploadMedia(mimeType: String, data: ByteArray): Result<String>
    fun roomMembershipObserver(): RoomMembershipObserver

    /**
     * Get a room info flow for a given room ID.
     * The flow will emit a new value whenever the room info is updated.
     * The flow will emit Optional.empty item if the room is not found.
     */
    fun getRoomInfoFlow(roomId: RoomId): Flow<Optional<RoomInfo>>

    /**
     * Get a room summary flow for a given room ID or alias.
     * The flow will emit a new value whenever the room summary is updated.
     * The flow will emit Optional.empty item if the room is not found.
     */
    fun getRoomSummaryFlow(roomIdOrAlias: RoomIdOrAlias): Flow<Optional<RoomSummary>>

    fun isMe(userId: UserId?) = userId == sessionId

    suspend fun trackRecentlyVisitedRoom(roomId: RoomId): Result<Unit>
    suspend fun getRecentlyVisitedRooms(): Result<List<RoomId>>

    /**
     * Resolves the given room alias to a roomID (and a list of servers), if possible.
     * @param roomAlias the room alias to resolve
     * @return the resolved room alias if any, an empty result if not found,or an error if the resolution failed.
     *
     */
    suspend fun resolveRoomAlias(roomAlias: RoomAlias): Result<Optional<ResolvedRoomAlias>>

    /**
     * Enables or disables the sending queue, according to the given parameter.
     *
     * The sending queue automatically disables itself whenever sending an
     * event with it failed (e.g. sending an event via the Timeline),
     * so it's required to manually re-enable it as soon as
     * connectivity is back on the device.
     */
    suspend fun setAllSendQueuesEnabled(enabled: Boolean)

    /**
     * Returns a flow of room IDs that have send queue being disabled.
     * This flow will emit a new value whenever the send queue is disabled for a room.
     */
    fun sendQueueDisabledFlow(): Flow<RoomId>

    /**
     * Return the server name part of the current user ID, using the SDK, and if a failure occurs,
     * compute it manually.
     */
    fun userIdServerName(): String

    /**
     * Execute generic GET requests through the SDKs internal HTTP client.
     */
    suspend fun getUrl(url: String): Result<ByteArray>

    /**
     * Get a room preview for a given room ID or alias. This is especially useful for rooms that the user is not a member of, or hasn't joined yet.
     */
    suspend fun getRoomPreview(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>): Result<NotJoinedRoom>

    /**
     * Returns the currently used sliding sync version.
     */
    suspend fun currentSlidingSyncVersion(): Result<SlidingSyncVersion>

    /**
     * Returns the available sliding sync versions for the current user.
     */
    suspend fun availableSlidingSyncVersions(): Result<List<SlidingSyncVersion>>

    fun canDeactivateAccount(): Boolean
    suspend fun deactivateAccount(password: String, eraseData: Boolean): Result<Unit>

    /**
     * Check if the user can report a room.
     */
    suspend fun canReportRoom(): Boolean

    /**
     * Return true if Livekit Rtc is supported, i.e. if Element Call is available.
     */
    suspend fun isLivekitRtcSupported(): Boolean

    /**
     * Returns the maximum file upload size allowed by the Matrix server.
     */
    suspend fun getMaxFileUploadSize(): Result<Long>

    suspend fun getUserRewards(shouldCheckRewardsIntimation: Boolean = false)
    fun dismissRewardsIntimation()

    suspend fun getZeroMessengerInvite(): Result<ZeroMessengerInvite>

    suspend fun isZeroProfileCompletionPending(): Boolean

    suspend fun completeZeroUserProfile(
        inviteCode: String, displayName: String, mimeType: String?, avatarData: ByteArray?
    ): Result<Unit>

    suspend fun deleteUserAccount(): Result<Unit>

    suspend fun linkZeroUserIfRequired(): Result<Unit>

    suspend fun verifyUserPassword(password: String): Result<Unit>

    suspend fun getUserZIds()

    suspend fun fetchUserWallets(): Result<List<ZeroWallet>>

    suspend fun addWallet(canAuthenticate: Boolean, token: String) : Result<Unit>

    suspend fun deleteWallet(walledId: String): Result<Unit>

    suspend fun joinZeroChannel(channelId: String): Result<String?>

    suspend fun fetchAllFeeds(followingFeeds: Boolean,
                              limit: Int,
                              skip: Int,
                              includeReplies: Boolean = true,
                              includeMeow: Boolean = true
    )

    suspend fun fetchAllMyFeeds(limit: Int,
                                skip: Int,
                                includeReplies: Boolean = true,
                                includeMeow: Boolean = true
    )

    suspend fun fetchFeedDetails(feedId: String,
                                 includeReplies: Boolean = true,
                                 includeMeow: Boolean = true
    ): Result<ZeroFeed?>

    suspend fun fetchFeedMedia(mediaId: String, isPreview: Boolean = true): Result<FeedMedia?>

    suspend fun fetchFeedReplies(
        feedId: String,
        limit: Int,
        skip: Int,
        includeReplies: Boolean = true,
        includeMeow: Boolean = true
    ): Result<List<ZeroFeed>>

    suspend fun addMeowToFeed(feed: ZeroFeed, meowAmount: Int): Result<ZeroFeed?>

    suspend fun checkZeroThirdWebWallet()

    suspend fun createNewFeed(content: String, attachment: CreateFeedMediaAttachment?, replyToPost: String?): Result<Unit>

    suspend fun fetchUrlMetaData(url: String): Result<ZeroLinkPreview?>

    suspend fun fetchAllUserFeeds(userId: String,
                                  limit: Int,
                                  skip: Int,
                                  includeReplies: Boolean = true,
                                  includeMeow: Boolean = true): Result<List<ZeroFeed>>

    suspend fun fetchFeedUserProfile(key: String): Result<FeedUserProfileView?>

    suspend fun fetchUserFollowingStatus(userId: String): Result<Boolean>

    suspend fun followUser(userId: String): Result<Boolean>

    suspend fun unFollowUser(userId: String): Result<Boolean>

    suspend fun getMeowPrice(): Result<ZeroMeowPrice>

    suspend fun getWalletTokens(walletAddress: String,
                                chainId: Int,
                                paginationParams: ZeroWalletTokensPaginationParams?
    ): Result<ZeroWalletTokensResponse>

    suspend fun getWalletTransactions(walletAddress: String,
                                      chainId: Int,
                                      paginationParams: ZeroWalletTransactionsPaginationParams?
    ): Result<ZeroWalletTransactionsResponse>

    suspend fun getTransactionReceipt(transactionId: String, chainId: Int): Result<ZeroWalletTransactionReceipt>

    suspend fun claimRewards(walletAddress: String): Result<String>

    suspend fun searchWalletRecipient(query: String): Result<List<ZeroWalletRecipient>>

    suspend fun transferToken(sender: String, recipient: String, chainId: Int, amount: String, token: String): Result<ZeroWalletTransactionReceipt>

    suspend fun getTokenInfo(tokenAddress: String): Result<ZeroWalletTokenInfo>

    suspend fun getTokenBalance(userAddress: String, tokenAddress: String): Result<ZeroWalletTokenBalance>

    suspend fun getTotalStaked(poolAddress: String): Result<String>

    suspend fun getStakingConfig(poolAddress: String): Result<ZeroStakingConfig>

    suspend fun getStakerStatusInfo(userAddress: String, poolAddress: String): Result<ZeroStakingStatus>

    suspend fun getStakeRewardsInfo(userAddress: String, poolAddress: String): Result<ZeroStakingUserRewardsInfo>

    suspend fun getStakingToken(poolAddress: String): Result<ZeroTokenAddress>

    suspend fun getRewardToken(poolAddress: String): Result<ZeroTokenAddress>

    suspend fun stakeAmount(userAddress: String, amount: String, poolAddress: String, tokenAddress: String): Result<String>

    suspend fun unstakeAmount(userAddress: String, amount: String, poolAddress: String): Result<String>

    suspend fun claimStakingRewards(userAddress: String, poolAddress: String): Result<String>
}

/**
 * Returns a room alias from a room alias name, or null if the name is not valid.
 * @param name the room alias name ie. the local part of the room alias.
 */
fun MatrixClient.roomAliasFromName(name: String): RoomAlias? {
    return name.takeIf { it.isNotEmpty() }
        ?.let { "#$it:${userIdServerName()}" }
        ?.takeIf { MatrixPatterns.isRoomAlias(it) }
        ?.let { tryOrNull { RoomAlias(it) } }
}
