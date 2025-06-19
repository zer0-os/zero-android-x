package io.element.android.support.zero.common.extension

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

inline fun <T> withSameScope(crossinline block: suspend () -> T) =
	CoroutineScope(Dispatchers.Unconfined).launch { block() }

inline fun <T> withScope(dispatcher: CoroutineDispatcher, crossinline block: suspend () -> T) =
	CoroutineScope(dispatcher).launch { block() }

inline fun <T> withIOScope(crossinline block: suspend () -> T) =
    CoroutineScope(Dispatchers.IO).launch { block() }

inline fun <T> withScopeAsync(dispatcher: CoroutineDispatcher, crossinline block: suspend () -> T) =
	CoroutineScope(dispatcher).async { block() }

inline fun <T> CoroutineScope.safeAsync(crossinline block: suspend () -> T): Deferred<Result<T>> =
    async {
        runCatching { block() }
    }

suspend inline fun <T> runOnMainThread(crossinline block: suspend () -> T) =
	withContext(Dispatchers.Main) { block() }

fun <T> FlowCollector<T>.emitInScope(
	value: T,
	scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
) = scope.launch { emit(value) }

@OptIn(ExperimentalTypeInference::class)
inline fun <T> callbackFlowWithAwait(
	@BuilderInference crossinline block: suspend ProducerScope<T>.() -> Unit
) = callbackFlow {
	block(this)
	awaitClose()
}

@OptIn(ExperimentalTypeInference::class)
inline fun <T> channelFlowWithAwait(
	@BuilderInference crossinline block: suspend ProducerScope<T>.() -> Unit
) = callbackFlow {
	block(this)
	awaitClose()
}

fun <T> runBlockingWithTimeOut(
	context: CoroutineContext = Dispatchers.IO,
	timeoutMillis: Long = 5_000L,
	block: suspend CoroutineScope.() -> T
) = runBlocking(context) { withTimeoutOrNull(timeoutMillis) { block() } }
