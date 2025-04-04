package io.element.android.features.zeroinvite.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class InvitePresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<InviteState> {
    @Composable
    override fun present(): InviteState {
        val coroutineScope = rememberCoroutineScope()
        val messengerInviteFlow = remember { mutableStateOf(ZeroMessengerInvite.empty()) }

        LaunchedEffect(Unit) {
            // Fetch user rewards
            coroutineScope.fetchMessengerInvite(messengerInviteFlow)
        }

        return InviteState(messengerInvite = messengerInviteFlow.value)
    }

    private fun CoroutineScope.fetchMessengerInvite(messengerInviteFlow: MutableState<ZeroMessengerInvite>) = launch {
        val result = client.getZeroMessengerInvite()
        val invite = result.getOrElse { ZeroMessengerInvite.empty() }
        messengerInviteFlow.value = invite
    }
}
