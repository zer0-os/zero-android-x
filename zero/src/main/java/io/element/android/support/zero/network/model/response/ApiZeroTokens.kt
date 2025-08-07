package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiZeroTokens(
	val price: Double?,
	val reference: String?,
    val diff: Double? = null
)
