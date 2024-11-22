package io.element.android.support.zero.network.model.request

import io.element.android.support.zero.datastore.converter.AppJson.toJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatrixUsersFilter(@SerialName("matrixIds") val matrixIds: List<String>? = null) {
	override fun toString() = toJson()

	companion object {
		fun newFilter(userIds: List<String>) = MatrixUsersFilter(userIds)
	}
}
