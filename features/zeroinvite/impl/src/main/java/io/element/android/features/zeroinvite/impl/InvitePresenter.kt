package io.element.android.features.zeroinvite.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import javax.inject.Inject

class InvitePresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<InviteState> {
    @Composable
    override fun present(): InviteState {
        val coroutineScope = rememberCoroutineScope()
        val messengerInvite = client.messengerInvite.collectAsState()

        LaunchedEffect(Unit) {
            // Fetch user rewards
            client.getZeroMessengerInvite()
        }

        return InviteState(messengerInvite = messengerInvite.value)
    }
}
