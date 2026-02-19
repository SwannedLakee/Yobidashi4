package jp.toastkid.yobidashi4.domain.repository.chat.dto

import jp.toastkid.yobidashi4.domain.model.chat.Source

data class ChatResponseItem(
    private val message: String,
    private val error: Boolean = false,
    private val image: Boolean = false,
    private val sources: List<Source> = emptyList<Source>(),
) {

    fun message() = message

    fun error() = error

    fun image() = image

    companion object {

        private val ERROR = ChatResponseItem("[ERROR]", error = true)

        private val OTHER = ChatResponseItem("[OTHER]", error = true)

        fun error() = ERROR

        fun other() = OTHER

    }

}