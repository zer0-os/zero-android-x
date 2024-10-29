package io.element.android.support.zero.data.delegate

interface Preferences {
    fun zosToken(): String

    suspend fun setZeroToken(token: String)

    fun matrixToken(): String

    suspend fun setMatrixToken(token: String)

    fun userId(): String

    suspend fun setUserId(id: String)
}
