package io.element.android.support.zero.network.model.request

import io.element.android.support.zero.datastore.converter.AppJson.toJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersFilter(
	@SerialName("filter") val name: String? = null,
	@SerialName("isMatrixEnabled") val isMatrixEnabled: Boolean = false,
	@SerialName("limit") val limit: Int? = null,
	val offset: Int? = null
) {
	override fun toString() = toJson()

    companion object {
        fun newNameFilter(query: String) =
            UsersFilter(name = query, isMatrixEnabled = true, limit = 50)

        fun emptyFilter() = UsersFilter()
    }
}
