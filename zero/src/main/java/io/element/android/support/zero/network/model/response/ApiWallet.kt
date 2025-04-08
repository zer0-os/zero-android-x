package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiWallet(
    //@SerialName("createdAt") val createdAt: Long? = 0L,
    val id: String,
    val isDefault: Boolean,
    val isMultiSig: Boolean,
    val name: String? = null,
    val networkId: String? = null,
    val publicAddress: String,
    //@SerialName("updatedAt") val updatedAt: Long? = 0L,
    val userId: String,
    val isThirdWeb: Boolean
)
