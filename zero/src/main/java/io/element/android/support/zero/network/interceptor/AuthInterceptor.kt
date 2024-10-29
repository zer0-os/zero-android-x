package io.element.android.support.zero.network.interceptor

import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.network.meta.NoAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import retrofit2.Invocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthInterceptor @Inject constructor(private val preferences: AppPreferences) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val invocation: Invocation? = request.tag(Invocation::class.java)
        val noAuth: NoAuth? = invocation?.method()?.getAnnotation(NoAuth::class.java)

        val requestBuilder: Request.Builder = request.newBuilder()
        if (noAuth == null) {
            val token = preferences.zosToken()
            if (token.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }
        val response = chain.proceed(requestBuilder.build())
        /*when (response.code) {
            HttpURLConnection.HTTP_UNAUTHORIZED,
            HttpURLConnection.HTTP_UNAVAILABLE ->
                if (StateBus.userState.isAuthorized) {
                    StateBus.onUserStateChanged(UserState.UNAUTHORIZED)
                }
        }*/
        return response
    }
}
