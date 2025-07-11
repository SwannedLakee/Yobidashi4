package jp.toastkid.yobidashi4.infrastructure.repository.web

import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import org.koin.core.annotation.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@Single
class WebHistoryFileStore : WebHistoryRepository {

    override fun add(title: String, url: String) {
        val webHistories = readAll()
        val candidate = webHistories.firstOrNull { it.title == title && it.url == url } ?: WebHistory(title, url, System.currentTimeMillis())

        val item = candidate.copy(lastVisitedTime = System.currentTimeMillis(), visitingCount = candidate.visitingCount + 1)

        val key = title + url
        val filtered = webHistories.filter { it.title + it.url != key }
        val newList = mutableListOf<WebHistory>().also {
            it.addAll(filtered)
            it.add(item)
        }

        Files.write(getPath(), newList.map { it.toTsv() })
    }

    override fun storeAll(items: List<WebHistory>) {
        Files.write(getPath(), items.map { it.toTsv() })
    }

    override fun delete(item: WebHistory) {
        Files.write(
            getPath(),
            readAll().minus(item).map { it.toTsv() }
        )
    }

    override fun readAll(): List<WebHistory> {
        val path = getPath()
        makeFolderIfNeed(path)

        if (Files.exists(path).not()) {
            return emptyList()
        }

        return BufferedReader(InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)).use { reader ->
            reader.readLines()
                .filter { it.isNotBlank() }
                .map {
                    val split = it.split(VALUE_DELIMITER)
                    WebHistory(
                        split[0],
                        if (split.size >= 2) split[1] else "",
                        if (split.size >= 3) split[2].toLongOrNull() ?: 0 else 0,
                        if (split.size >= 4) split[3].toIntOrNull() ?: 0 else 0
                    )
                }
        }
    }

    private fun getPath(): Path {
        return Path.of(PATH_TO_HISTORY)
    }

    private fun makeFolderIfNeed(path: Path) {
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }
    }

    override fun clear() {
        Files.delete(getPath())
    }

}

private const val PATH_TO_HISTORY = "temporary/web/history.tsv"

private const val VALUE_DELIMITER = "\t"
