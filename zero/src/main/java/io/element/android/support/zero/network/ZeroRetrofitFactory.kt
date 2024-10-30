package io.element.android.support.zero.network

import io.element.android.libraries.core.uri.ensureTrailingSlash
import io.element.android.support.zero.config.ZeroConfig
import io.element.android.support.zero.network.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class ZeroRetrofitFactory(
    authInterceptor: AuthInterceptor,
    loggingInterceptor: HttpLoggingInterceptor
) {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(NETWORK_CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(NETWORK_READ_TIME_OUT, TimeUnit.SECONDS)
        .build()

    private val json: Json = Json { ignoreUnknownKeys = true }
    private val jsonConverter = json.asConverterFactory("application/json".toMediaType())

    val baseClient: Retrofit =
        Retrofit.Builder()
            .baseUrl(ZeroConfig.environment.zosUrl.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(jsonConverter)
            .build()

    fun apiClient(): Retrofit =
        Retrofit.Builder()
            .baseUrl(ZeroConfig.environment.apiUrl.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(jsonConverter)
            .build()

    companion object {
        private const val NETWORK_CONNECT_TIME_OUT = 15L
        private const val NETWORK_READ_TIME_OUT = 30L
    }
}
