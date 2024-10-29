package io.element.android.support.zero.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.service.ZeroAuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ZeroServiceModule {

    @Singleton
    @Provides
    fun provideZeroAuthService(retrofit: ZeroRetrofitFactory): ZeroAuthService =
        retrofit.baseClient.create(ZeroAuthService::class.java)
}
