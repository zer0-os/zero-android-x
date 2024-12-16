package io.element.android.support.zero.network.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.POST

interface ZeroAccountService {
    @POST("api/v2/accounts/delete")
    suspend fun deleteUserAccount(): Response<ResponseBody>
}
