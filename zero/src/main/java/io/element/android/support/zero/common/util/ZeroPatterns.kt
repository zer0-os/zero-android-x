package io.element.android.support.zero.common.util

object ZeroPatterns {
    private const val ZERO_USER_MENTION_PATTERN = """@\[(.+?)\]\(user:(.+?)\)"""
    val ZERO_USER_MENTION_REGEX = ZERO_USER_MENTION_PATTERN.toRegex()

    private const val MATRIX_USER_MENTION_PATTERN = """\[@([a-f0-9\-]+:[a-zA-Z0-9\.\-]+)\]\(https:\/\/matrix\.to\/#\/@\1\)"""
    val MATRIX_USER_MENTION_REGEX = MATRIX_USER_MENTION_PATTERN.toRegex()

    const val MATRIX_USER_LINK_BASE_URL = "https://matrix.to/#/@"

    fun correctlyFormattedHtmlString(
        text: String?,
        messageBody: String,
        domain: String
    ): String {
        val baseUrl = MATRIX_USER_LINK_BASE_URL
        val htmlBody: String = messageBody
        val actualText: String = (text.orEmpty()).replace("\n", "<br>")

        // Use a regular expression to find user mentions in the format @[Name](user:UUID)
        val regexPattern = ZERO_USER_MENTION_REGEX
        if (htmlBody.isNotBlank()) {
            val actualMentionRegex = MATRIX_USER_MENTION_REGEX
            val matches = actualMentionRegex.findAll(actualText).toList()
            if (matches.isNotEmpty() && htmlBody.contains("<a href=")) {
                return htmlBody
            } else {
                // Replace matches with the appropriate HTML anchor tags
                val modifiedBody = regexPattern.replace(actualText) {
                    "<a href=\"$baseUrl${it.groupValues[2]}:$domain\">@${it.groupValues[2]}:$domain</a>"
                }
                return modifiedBody
            }
        } else {
            return htmlBody
        }
    }
}
