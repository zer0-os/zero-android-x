package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.MatrixUsersFilter
import io.element.android.support.zero.network.model.response.ApiUser
import retrofit2.http.Body
import retrofit2.http.POST

interface ZeroMatrixUserService {
    @POST("matrix/users/zero")
    suspend fun getMatrixUsers(@Body matrixIds: MatrixUsersFilter): List<ApiUser>
}
