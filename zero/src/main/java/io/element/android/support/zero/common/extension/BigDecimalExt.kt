/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.extension

import java.math.BigDecimal

fun BigDecimal.isPositive(): Boolean = this > BigDecimal.ZERO
