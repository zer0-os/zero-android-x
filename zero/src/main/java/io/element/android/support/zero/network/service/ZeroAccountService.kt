package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ZeroAccountService {
    @POST("api/v2/accounts/delete")
    suspend fun deleteUserAccount(): Response<ResponseBody>

    @POST(value = "matrix/link-zero-user")
    suspend fun linkZeroUser(@Body payload: LinkZeroUserRequest)
}
