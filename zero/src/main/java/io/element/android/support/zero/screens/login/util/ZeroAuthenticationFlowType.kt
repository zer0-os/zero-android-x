package io.element.android.support.zero.screens.login.util

import kotlin.math.max

enum class ZeroAuthenticationFlowType {
	WEB3,
	EMAIL;

	companion object {
		fun get(index: Int) = entries.getOrNull(index) ?: EMAIL

		fun indexOf(flowType: ZeroAuthenticationFlowType) = max(entries.indexOf(flowType), 0)
	}
}
