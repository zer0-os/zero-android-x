package io.element.android.support.zero.screens.login.util

import kotlin.math.max

enum class LoginFlowType {
	WEB3,
	EMAIL;

	companion object {
		fun get(index: Int) = entries.getOrNull(index) ?: EMAIL

		fun indexOf(flowType: LoginFlowType) = max(entries.indexOf(flowType), 0)
	}
}
