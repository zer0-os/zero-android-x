package io.element.android.support.zero.network.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.service.ZeroAccountService
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroChannelService
import io.element.android.support.zero.network.service.ZeroConversationService
import io.element.android.support.zero.network.service.ZeroInviteService
import io.element.android.support.zero.network.service.ZeroMatrixUserService
import io.element.android.support.zero.network.service.ZeroRewardService
import io.element.android.support.zero.network.service.ZeroUserService

@Module
@ContributesTo(AppScope::class)
object ZeroServiceModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroAuthService(retrofit: ZeroRetrofitFactory): ZeroAuthService =
        retrofit.baseClient.create(ZeroAuthService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroConversationService(retrofit: ZeroRetrofitFactory): ZeroConversationService =
        retrofit.baseClient.create(ZeroConversationService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroUserService(retrofit: ZeroRetrofitFactory): ZeroUserService =
        retrofit.apiClient.create(ZeroUserService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroMatrixUserService(retrofit: ZeroRetrofitFactory): ZeroMatrixUserService =
        retrofit.baseClient.create(ZeroMatrixUserService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroRewardService(retrofit: ZeroRetrofitFactory): ZeroRewardService =
        retrofit.baseClient.create(ZeroRewardService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroInviteService(retrofit: ZeroRetrofitFactory): ZeroInviteService =
        retrofit.baseClient.create(ZeroInviteService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroAccountService(retrofit: ZeroRetrofitFactory): ZeroAccountService =
        retrofit.baseClient.create(ZeroAccountService::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideZeroChannelService(retrofit: ZeroRetrofitFactory): ZeroChannelService =
        retrofit.baseClient.create(ZeroChannelService::class.java)
}
