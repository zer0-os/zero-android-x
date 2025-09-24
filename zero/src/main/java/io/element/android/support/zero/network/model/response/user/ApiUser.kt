package io.element.android.support.zero.network.model.response.user

import io.element.android.support.zero.network.model.response.wallet.ApiWallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ApiBaseMember {
    val id: String
    val matrixId: String?
}

@Serializable
data class ApiUser(
    override val id: String,
    override val matrixId: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("profileSummary") val profile: ApiProfile? = null,
    val profileId: String? = null,
    private val profileImage: String? = null,
    val type: String? = null,
    val isOnline: Boolean = false,
    val lastActiveAt: Long? = 0L,
    //@SerialName("createdAt") private val createdAt: Long? = 0L,
    //@SerialName("updatedAt") private val updatedAt: Long? = 0L,
    val isAdmin: Boolean = false,
    val isAssistantAdmin: Boolean = false,
    val summary: String? = null,
    val handle: String? = null,
    val isAMemberOfWorlds: Boolean? = false,
    val matrixAccessToken: String? = null,
    val primaryZID: String? = null,
    val primaryWalletAddress: String? = null,
    val zeroWalletAddress: String? = null,
    val wallets: List<ApiWallet>? = null,
    val primaryWallet: ApiWallet? = null,
    val subscriptions: ZeroSubscription? = null
) : ApiBaseMember {
    val firstName
        get() = profile?.firstName.orEmpty()

    val name
        get() = _name ?: profile?.run { "$firstName $lastName" }

    val avatar
        get() = profileImage ?: profile?.profileImage

    val walletAddress
        get() = zeroWalletAddress

    val thirdWebWallet
        get() = wallets?.firstOrNull { it.isThirdWeb }
}

@Serializable
data class ZeroSubscription(
    val wilderPro: Boolean,
    val zeroPro: Boolean
)
