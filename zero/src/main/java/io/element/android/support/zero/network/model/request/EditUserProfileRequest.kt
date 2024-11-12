package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EditUserProfileRequest(val profileData: EditUserProfileData) {
	companion object {
		fun newRequest(
			firstName: String? = null,
			image: String? = null,
			profileZid: String? = null
		): EditUserProfileRequest {
			return EditUserProfileRequest(
				profileData =
				EditUserProfileData(
					firstName = firstName,
					profileImage = image,
					primaryZID = profileZid
				)
			)
		}
	}
}

@Serializable
data class EditUserProfileData(
	val firstName: String? = null,
	val profileImage: String? = null,
	val primaryZID: String? = null
)
