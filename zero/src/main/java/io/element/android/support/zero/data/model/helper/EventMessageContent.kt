package io.element.android.support.zero.data.model.helper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventMessageContent(
    val sender: String,
    val content: MessageContent,
    @SerialName("room_id") val roomId: String? = null,
    @SerialName("origin_server_ts") val originServerTs: Long
)

@Serializable
data class MessageContent(
    @SerialName("optimisticId") val optimisticID: String? = null,
    val info: EncryptedAttachmentInfo? = null,
    @SerialName("localId") val localID: String? = null,
    val file: EncryptedAttachmentFile? = null,
    @SerialName("msgtype") val messageType: String,
    val body: String? = null,
    val url: String? = null
)

@Serializable
data class EncryptedAttachment(
    @SerialName("optimisticId") val optimisticID: String? = null,
    val info: EncryptedAttachmentInfo,
    @SerialName("localId") val localID: String? = null,
    val file: EncryptedAttachmentFile,
    @SerialName("msgtype") val messageType: String,
    val body: String? = null
) {
    val key: String?
        get() = file.key?.k

    val iv: String?
        get() = file.iv
}

@Serializable
data class EncryptedAttachmentInfo(
    @SerialName("mimetype") val mimeType: String,
    val name: String? = null,
    @SerialName("optimisticId") val optimisticID: String,
    @SerialName("rootMessageId") val rootMessageID: String,
    val size: Int,
    val width: Float? = null,
    val height: Float? = null,
    val w: Float? = null,
    val h: Float? = null
)

@Serializable
data class EncryptedAttachmentFile(
    val hashes: Map<String, String>? = null,
    val iv: String? = null,
    val key: EncryptedAttachmentKey? = null,
    val url: String,
    val v: String? = null
)

@Serializable
data class EncryptedAttachmentKey(
    val alg: String,
    val ext: Boolean,
    val k: String,
    @SerialName("key_ops") val keyOps: List<String>,
    val kty: String
)

val MessageContent.isImage: Boolean
    get() = messageType == "m.image"

val MessageContent.isVideo: Boolean
    get() = messageType == "m.video"

val MessageContent.isRemoteGif: Boolean
    get() = isImage
        && file == null
        && url?.isNotBlank() == true
        && !url.startsWith("mxc://")
