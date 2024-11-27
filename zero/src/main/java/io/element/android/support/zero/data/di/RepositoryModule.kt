package io.element.android.support.zero.data.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.repository.AuthRepository
import io.element.android.support.zero.data.repository.AuthRepositoryImpl
import io.element.android.support.zero.data.repository.ConversationRepository
import io.element.android.support.zero.data.repository.ConversationRepositoryImpl
import io.element.android.support.zero.data.repository.InviteRepository
import io.element.android.support.zero.data.repository.InviteRepositoryImpl
import io.element.android.support.zero.data.repository.RewardsRepository
import io.element.android.support.zero.data.repository.RewardsRepositoryImpl
import io.element.android.support.zero.data.repository.UserRepository
import io.element.android.support.zero.data.repository.UserRepositoryImpl
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroConversationService
import io.element.android.support.zero.network.service.ZeroInviteService
import io.element.android.support.zero.network.service.ZeroMatrixUserService
import io.element.android.support.zero.network.service.ZeroRewardService
import io.element.android.support.zero.network.service.ZeroUserService

@Module
@ContributesTo(AppScope::class)
object RepositoryModule {

    @Provides
    @SingleIn(AppScope::class)
    fun bindAuthRepository(
        preferences: Preferences,
        zeroAuthService: ZeroAuthService,
        dataCleaner: DataCleaner
    ): AuthRepository = AuthRepositoryImpl(preferences, zeroAuthService, dataCleaner)

    @Provides
    @SingleIn(AppScope::class)
    fun bindConversationRepository(
        zeroConversationService: ZeroConversationService
    ): ConversationRepository = ConversationRepositoryImpl(zeroConversationService)

    @Provides
    @SingleIn(AppScope::class)
    fun bindUserRepository(
        zeroUserService: ZeroUserService,
        zeroMatrixUserService: ZeroMatrixUserService
    ): UserRepository = UserRepositoryImpl(zeroUserService, zeroMatrixUserService)

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
}
