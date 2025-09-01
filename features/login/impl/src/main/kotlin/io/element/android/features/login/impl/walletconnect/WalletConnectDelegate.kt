/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.walletconnect

import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object WalletConnectDelegate: AppKit.ModalDelegate {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _wcEventModels: MutableSharedFlow<Modal.Model?> = MutableSharedFlow()
    val wcEventModels: SharedFlow<Modal.Model?> =  _wcEventModels.asSharedFlow()

    init {
        AppKit.setDelegate(this)
    }

    override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
        scope.launch {
            _wcEventModels.emit(state)
        }
    }

    override fun onError(error: Modal.Model.Error) {
        scope.launch {
            _wcEventModels.emit(error)
        }
    }

    override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
        scope.launch {
            _wcEventModels.emit(proposal)
        }
    }

    override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
        scope.launch {
            _wcEventModels.emit(request)
        }
    }

    override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
        scope.launch {
            _wcEventModels.emit(approvedSession)
        }
    }

    override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
        scope.launch {
            _wcEventModels.emit(deletedSession)
        }
    }

    @Deprecated("Use onSessionEvent(Modal.Model.Event) instead. Using both will result in duplicate events.", replaceWith = ReplaceWith("onEvent(event)"))
    override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
        scope.launch {
            _wcEventModels.emit(sessionEvent)
        }
    }

    override fun onSessionExtend(session: Modal.Model.Session) {
        scope.launch {
            _wcEventModels.emit(session)
        }
    }

    override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
        scope.launch {
            _wcEventModels.emit(rejectedSession)
        }
    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        scope.launch {
            _wcEventModels.emit(response)
        }
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        scope.launch {
            _wcEventModels.emit(updatedSession)
        }
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.Event) {
        scope.launch {
            _wcEventModels.emit(sessionEvent)
        }
    }

    override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Modal.Model.SessionAuthenticateResponse) {
        scope.launch {
            _wcEventModels.emit(sessionAuthenticateResponse)
        }
    }

    override fun onSIWEAuthenticationResponse(response: Modal.Model.SIWEAuthenticateResponse) {
        println("SIWE response: $response")
    }
}
