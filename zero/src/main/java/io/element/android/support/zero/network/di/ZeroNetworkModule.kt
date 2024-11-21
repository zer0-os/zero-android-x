package io.element.android.support.zero.network.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.interceptor.AuthInterceptor
import okhttp3.logging.HttpLoggingInterceptor

@Module
@ContributesTo(AppScope::class)
object NetworkModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAuthInterceptor(preferences: AppPreferences) = AuthInterceptor(preferences)

    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRetrofit(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) = ZeroRetrofitFactory(authInterceptor, loggingInterceptor)
}
