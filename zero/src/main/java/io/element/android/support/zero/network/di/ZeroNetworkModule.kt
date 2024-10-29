package io.element.android.support.zero.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.network.ZeroRetrofitFactory
import io.element.android.support.zero.network.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val NETWORK_CONNECT_TIME_OUT = 15L
    private const val NETWORK_READ_TIME_OUT = 30L

    @Singleton
    @Provides
    fun provideAuthInterceptor(preferences: AppPreferences) = AuthInterceptor(preferences)

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NETWORK_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_READ_TIME_OUT, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideJson() = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun provideJsonConverter(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, json: Converter.Factory) = ZeroRetrofitFactory(client, json)
}
