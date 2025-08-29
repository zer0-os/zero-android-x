package io.element.android.support.zero.network.model.response.wallet

import kotlinx.serialization.Serializable

@Serializable
data class ApiUserWallets(
    val wallets: List<ApiWallet>
)

@Serializable
data class ApiWallet(
    //@SerialName("createdAt") val createdAt: Long? = 0L,
    val id: String,
    val isDefault: Boolean,
    val isMultiSig: Boolean? = null,
    val name: String? = null,
    val networkId: String? = null,
    val publicAddress: String,
    //@SerialName("updatedAt") val updatedAt: Long? = 0L,
    val userId: String? = null,
    val isThirdWeb: Boolean,
    val canAuthenticate: Boolean? = null
)
