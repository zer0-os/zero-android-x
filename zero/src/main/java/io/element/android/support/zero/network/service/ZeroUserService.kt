package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.EditUserProfileRequest
import io.element.android.support.zero.network.model.request.UsersFilter
import io.element.android.support.zero.network.model.response.user.ApiUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface ZeroUserService {
    @GET(value = "users/current")
    suspend fun getCurrentUser(): ApiUser

    @GET("v2/users/searchInNetworksByName")
    suspend fun getUsers(@Query("filter") filter: String = UsersFilter.emptyFilter().toString()): List<ApiUser>?

    @PUT(value = "v2/users/profile")
    suspend fun updateProfile(@Body profile: EditUserProfileRequest)
}
