package io.element.android.support.zero.network.model.response.user

import kotlinx.serialization.Serializable

interface ApiBaseProfile {
	val firstName: String?
	val lastName: String?
	val profileImage: String?
}

@Serializable
data class ApiProfile(
	val id: String,
	val userId: String? = null,
	override val firstName: String? = null,
	override val lastName: String? = null,
	override val profileImage: String? = null,
	val gender: String? = null,
	val summary: String? = null,
	val backgroundImage: String? = null,
	//val createdAt: Long? = 0L
) : ApiBaseProfile
