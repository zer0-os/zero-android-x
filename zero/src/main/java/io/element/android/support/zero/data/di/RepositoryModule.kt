package io.element.android.support.zero.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.repository.AccountRepository
import io.element.android.support.zero.data.repository.AccountRepositoryImpl
import io.element.android.support.zero.data.repository.AuthRepository
import io.element.android.support.zero.data.repository.AuthRepositoryImpl
import io.element.android.support.zero.data.repository.ChannelRepository
import io.element.android.support.zero.data.repository.ChannelRepositoryImpl
import io.element.android.support.zero.data.repository.ConversationRepository
import io.element.android.support.zero.data.repository.ConversationRepositoryImpl
import io.element.android.support.zero.data.repository.FeedRepository
import io.element.android.support.zero.data.repository.FeedRepositoryImpl
import io.element.android.support.zero.data.repository.FeedUserRepository
import io.element.android.support.zero.data.repository.FeedUserRepositoryImpl
import io.element.android.support.zero.data.repository.InviteRepository
import io.element.android.support.zero.data.repository.InviteRepositoryImpl
import io.element.android.support.zero.data.repository.MetaDataRepository
import io.element.android.support.zero.data.repository.MetaDataRepositoryImpl
import io.element.android.support.zero.data.repository.RewardsRepository
import io.element.android.support.zero.data.repository.RewardsRepositoryImpl
import io.element.android.support.zero.data.repository.StakeRepository
import io.element.android.support.zero.data.repository.StakeRepositoryImpl
import io.element.android.support.zero.data.repository.UserRepository
import io.element.android.support.zero.data.repository.UserRepositoryImpl
import io.element.android.support.zero.data.repository.WalletRepository
import io.element.android.support.zero.data.repository.WalletRepositoryImpl
import io.element.android.support.zero.data.repository.ZeroCoreRepository
import io.element.android.support.zero.network.service.ZeroAccountService
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroChannelService
import io.element.android.support.zero.network.service.ZeroConversationService
import io.element.android.support.zero.network.service.ZeroFeedService
import io.element.android.support.zero.network.service.ZeroFeedUserService
import io.element.android.support.zero.network.service.ZeroInviteService
import io.element.android.support.zero.network.service.ZeroMatrixUserService
import io.element.android.support.zero.network.service.ZeroMetaDataService
import io.element.android.support.zero.network.service.ZeroRewardService
import io.element.android.support.zero.network.service.ZeroStakeService
import io.element.android.support.zero.network.service.ZeroUserService
import io.element.android.support.zero.network.service.ZeroWalletService

@BindingContainer
@ContributesTo(AppScope::class)
object RepositoryModule {

    @Provides
    @SingleIn(AppScope::class)
    fun bindCoreRepository(
        authRepository: AuthRepository,
        accountRepository: AccountRepository,
        conversationRepository: ConversationRepository,
        channelRepository: ChannelRepository,
        feedRepository: FeedRepository,
        feedUserRepository: FeedUserRepository,
        inviteRepository: InviteRepository,
        rewardsRepository: RewardsRepository,
        userRepository: UserRepository,
        metaDataRepository: MetaDataRepository,
        walletRepository: WalletRepository,
        stakeRepository: StakeRepository,
    ): ZeroCoreRepository? = ZeroCoreRepository(
        auth = authRepository,
        account = accountRepository,
        channel = channelRepository,
        conversation = conversationRepository,
        feed = feedRepository,
        feedUser = feedUserRepository,
        invite = inviteRepository,
        rewards = rewardsRepository,
        user = userRepository,
        metaData = metaDataRepository,
        wallet = walletRepository,
        stake = stakeRepository,
    )

    @Provides
    @SingleIn(AppScope::class)
    fun bindAuthRepository(
        preferences: Preferences,
        zeroAuthService: ZeroAuthService,
        zeroUserService: ZeroUserService,
        dataCleaner: DataCleaner
    ): AuthRepository = AuthRepositoryImpl(preferences, zeroAuthService, zeroUserService, dataCleaner)

    @Provides
    @SingleIn(AppScope::class)
    fun bindConversationRepository(
        zeroConversationService: ZeroConversationService
    ): ConversationRepository = ConversationRepositoryImpl(zeroConversationService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindUserRepository(
        zeroUserService: ZeroUserService,
        zeroMatrixUserService: ZeroMatrixUserService,
        preferences: Preferences
    ): UserRepository = UserRepositoryImpl(zeroUserService, zeroMatrixUserService, preferences)

    @Provides
    @SingleIn(AppScope::class)
    fun bindRewardsRepository(
        zeroRewardService: ZeroRewardService,
        preferences: Preferences
    ): RewardsRepository = RewardsRepositoryImpl(preferences, zeroRewardService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindInviteRepository(
        zeroInviteService: ZeroInviteService
    ): InviteRepository = InviteRepositoryImpl(zeroInviteService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindAccountRepository(
        zeroAccountService: ZeroAccountService,
        zeroUserService: ZeroUserService,
        preferences: Preferences
    ): AccountRepository = AccountRepositoryImpl(zeroAccountService, zeroUserService, preferences)

    @Provides
    @SingleIn(AppScope::class)
    fun bindChannelRepository(
        zeroChannelService: ZeroChannelService
    ): ChannelRepository = ChannelRepositoryImpl(zeroChannelService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindFeedRepository(
        zeroFeedService: ZeroFeedService
    ): FeedRepository = FeedRepositoryImpl(zeroFeedService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindFeedUserRepository(
        zeroFeedUserService: ZeroFeedUserService
    ): FeedUserRepository = FeedUserRepositoryImpl(zeroFeedUserService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindMetaDataRepository(
        zeroMetaDataService: ZeroMetaDataService
    ): MetaDataRepository = MetaDataRepositoryImpl(zeroMetaDataService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindWalletRepository(
        zeroUserService: ZeroUserService,
        zeroWalletService: ZeroWalletService
    ): WalletRepository = WalletRepositoryImpl(zeroUserService, zeroWalletService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindStakeRepository(
        zeroStakeService: ZeroStakeService
    ): StakeRepository = StakeRepositoryImpl(zeroStakeService)
}
