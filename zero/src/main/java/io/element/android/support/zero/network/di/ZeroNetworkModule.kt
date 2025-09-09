package io.element.android.support.zero.network.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.interceptor.AuthInterceptor
import okhttp3.logging.HttpLoggingInterceptor

@BindingContainer
@ContributesTo(AppScope::class)
object NetworkModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAuthInterceptor(preferences: AppPreferences): AuthInterceptor = AuthInterceptor(preferences)

    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRetrofit(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): ZeroRetrofitFactory = ZeroRetrofitFactory(authInterceptor, loggingInterceptor)
}
