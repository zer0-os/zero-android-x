package io.element.android.support.zero.common.util

enum class UserState {
	AUTHORIZED,
	ACCESS_TOKEN_EXPIRED,
	UNAUTHORIZED,
	UNIDENTIFIED,
}

val UserState.isAuthorized
	get() = this == UserState.AUTHORIZED
