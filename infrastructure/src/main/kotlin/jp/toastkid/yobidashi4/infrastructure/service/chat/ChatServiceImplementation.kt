package jp.toastkid.yobidashi4.infrastructure.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.infrastructure.model.chat.CHAT
import jp.toastkid.yobidashi4.infrastructure.model.chat.IMAGE_GENERATOR
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder
import java.util.concurrent.atomic.AtomicReference

@Single
class ChatServiceImplementation : ChatService, KoinComponent {

    private val chatHolder: AtomicReference<Chat> = AtomicReference(Chat())

    private val setting: Setting by inject()

    private val repository: ChatRepository by inject(parameters = {
        ParametersHolder(mutableListOf(setting.chatApiKey(), CHAT))
    })

    private val imageGeneratorRepository: ChatRepository by inject(parameters = {
        ParametersHolder(mutableListOf(setting.chatApiKey(), IMAGE_GENERATOR))
    })

    override fun send(messages: MutableList<ChatMessage>, image: Boolean, onUpdate: (ChatResponseItem?) -> Unit): String? {
        val chat = Chat(messages)

        (if (image) imageGeneratorRepository else repository)
            .request(chat.makeContent(image)) {
            onUpdate(it)
        }

        return null
    }

    override fun setChat(chat: Chat) {
        chatHolder.set(chat)
    }

    override fun getChat(): Chat {
        return chatHolder.get()
    }

    override fun messages(): List<ChatMessage> {
        return chatHolder.get().list()
    }

}