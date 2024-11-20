package io.element.android.support.zero.network.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroConversationService
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
    fun provideZeroRewardService(retrofit: ZeroRetrofitFactory): ZeroRewardService =
        retrofit.baseClient.create(ZeroRewardService::class.java)
}
