package jp.toastkid.yobidashi4.infrastructure.repository.chat

import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
import jp.toastkid.yobidashi4.infrastructure.repository.factory.HttpUrlConnectionFactory
import jp.toastkid.yobidashi4.infrastructure.service.chat.ChatStreamParser
import org.koin.core.annotation.Factory
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URI
import java.nio.charset.StandardCharsets

@Factory
class ChatApi(
    apiKey: String,
    apiUrl: String,
) : ChatRepository {

    private val httpUrlConnectionFactory = HttpUrlConnectionFactory()

    private val url = URI("$apiUrl$apiKey").toURL()

    override fun request(content: String, streamLineConsumer: (ChatResponseItem?) -> Unit) {
        val connection = httpUrlConnectionFactory.invoke(url) ?: return
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"
        connection.doInput = true
        connection.doOutput = true
        connection.readTimeout = 60_000
        connection.connect()

        BufferedWriter(OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)).use {
            LoggerFactory.getLogger(javaClass).debug(content)
            it.write(content)
        }

        if (connection.responseCode != 200) {
            LoggerFactory.getLogger(javaClass).error("return error ${connection.responseCode} ${String(connection.errorStream.readAllBytes())}")
            return
        }

        val parser = ChatStreamParser()
        BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use {
            var line = it.readLine()
            while (line != null) {
                val response = parser.invoke(line)
                if (response != null) {
                    streamLineConsumer(response)
                    LoggerFactory.getLogger(javaClass).debug(line)
                }

                line = it.readLine()
            }
        }
    }

}
