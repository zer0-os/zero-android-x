package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiZeroTokens(
	val diff: Double,
	val price: Double,
	val reference: String
)
