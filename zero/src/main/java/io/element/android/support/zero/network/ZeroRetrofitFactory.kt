package io.element.android.support.zero.network

import io.element.android.libraries.core.uri.ensureTrailingSlash
import io.element.android.support.zero.config.ZeroConfig
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
internal class ZeroRetrofitFactory(
    private val okHttpClient: OkHttpClient,
    private val json: Converter.Factory
) {
    val baseClient: Retrofit =
        Retrofit.Builder()
            .baseUrl(ZeroConfig.environment.zosUrl.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(json)
            .build()

    fun apiClient(): Retrofit =
        Retrofit.Builder()
            .baseUrl(ZeroConfig.environment.apiUrl.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(json)
            .build()
}
