/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ZeroLogService {

    private val db by lazy { FirebaseFirestore.getInstance() }

    private var userId: String? = null
    private var userName: String? = null

    private val logId: String by lazy {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        formatter.format(Date())
    }

    fun setup(userId: String, userName: String) {
        this.userId = userId
        this.userName = userName
    }

    fun logEvent(eventName: String, category: String, parameters: Map<String, Any> = emptyMap()) {
        val currentUserId = userId
        if (currentUserId != null) {
            val documentUserId = userName?.let {
                "${it.trim()}(${currentUserId.trim()})"
            } ?: currentUserId.trim()

            try {
                val mappedParameters = parameters.mapValues { it.value.toString() }
                /*withIOScope {
                    db
                        .collection("zero_logs_android")
                        .document(documentUserId)
                        .collection(logId)
                        .document(eventName)
                        .collection(category)
                        .add(parameters.mapValues { it.value.toString() })
                        .await()
                }*/
                db
                    .collection("zero_logs_android")
                    .document(documentUserId)
                    .collection(logId)
                    .document(eventName)
                    .collection(category)
                    .add(mappedParameters)
                    .addOnSuccessListener {
                        println("Event Logged: Success")
                    }
                    .addOnFailureListener { e ->
                        println("❌ Failed to log event: ${e.localizedMessage}")
                    }
            } catch (e: Exception) {
                println("❌ Exception while logging event: ${e.localizedMessage}")
            }
        }
    }
}
