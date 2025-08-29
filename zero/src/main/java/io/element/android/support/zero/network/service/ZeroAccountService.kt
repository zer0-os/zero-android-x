package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import io.element.android.support.zero.network.model.request.ResetUserPasswordRequest
import io.element.android.support.zero.network.model.response.wallet.ApiUserWallets
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZeroAccountService {
    @POST("api/v2/accounts/delete")
    suspend fun deleteUserAccount(): Response<ResponseBody>

    @POST(value = "matrix/link-zero-user")
    suspend fun linkZeroUser(@Body payload: LinkZeroUserRequest)

    @POST(value = "matrix/admin/reset-password")
    suspend fun resetAccountPassword(@Body payload: ResetUserPasswordRequest): Response<ResponseBody>

    @GET("api/v2/users/zids")
    suspend fun fetchUserZIds(): List<String>

    @GET(value = "api/v2/accounts/wallets")
    suspend fun fetchUserWallets(): ApiUserWallets
}
