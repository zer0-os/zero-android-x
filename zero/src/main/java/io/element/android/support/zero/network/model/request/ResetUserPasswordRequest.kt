package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable data class ResetUserPasswordRequest(val password: String)
