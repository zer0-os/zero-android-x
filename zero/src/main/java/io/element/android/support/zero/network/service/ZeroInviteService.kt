package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.response.ApiInvite
import retrofit2.http.POST
import retrofit2.http.Path

interface ZeroInviteService {

    @POST(value = "invite/{invite_code}/validate")
    suspend fun validateInvite(@Path("invite_code") inviteCode: String)

    @POST(value = "invite")
    suspend fun fetchMessengerInvite(): ApiInvite
}
