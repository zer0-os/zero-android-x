/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.getBestName

@Composable
fun MatrixUserHeader(
    matrixUser: MatrixUser?,
    modifier: Modifier = Modifier,
    // TODO handle click on this item, to let the user be able to update their profile.
    // onClick: () -> Unit,
) {
    if (matrixUser == null) {
        MatrixUserHeaderPlaceholder(modifier = modifier)
    } else {
        MatrixUserHeaderContent(
            matrixUser = matrixUser,
            modifier = modifier,
            // onClick = onClick
        )
    }
}

@Composable
private fun MatrixUserHeaderContent(
    matrixUser: MatrixUser,
    modifier: Modifier = Modifier,
    // onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            // .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(
            modifier = Modifier
                .padding(vertical = 12.dp),
            avatarData = matrixUser.getAvatarData(size = AvatarSize.UserHeader),
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                // Name
                Text(
                    modifier = Modifier.clipToBounds(),
                    text = matrixUser.getBestName(),
                    maxLines = 1,
                    style = ElementTheme.zeroTypography.fontHeadingSmMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = ElementTheme.colors.textPrimary,
                )

                if (matrixUser.isZeroProSubscriber) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        imageVector = CompoundIcons.Verified(),
                        contentDescription = null,
                        tint = ElementTheme.colors.zeroBrandColor
                    )
                }
            }
            // zero id
            if (matrixUser.primaryZeroId.isNullOrEmpty().not()) {
                Text(
                    text = matrixUser.primaryZeroId!!,
                    style = ElementTheme.zeroTypography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}

@PreviewsDayNight
@Composable
internal fun MatrixUserHeaderPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) = ElementPreview {
    MatrixUserHeader(matrixUser)
}
