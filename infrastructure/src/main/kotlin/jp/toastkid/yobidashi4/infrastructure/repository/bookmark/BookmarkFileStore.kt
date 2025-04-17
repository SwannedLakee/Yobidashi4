package jp.toastkid.yobidashi4.infrastructure.repository.bookmark

import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Single
class BookmarkFileStore : BookmarkRepository {

    override fun list(): List<Bookmark> {
        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).map {
            val split = it.split("\t")
            Bookmark(
                title = split[0],
                url = split[1]
            )
        }
    }

    override fun add(item: Bookmark) {
        save(list().plus(item))
    }

    private fun save(list: List<Bookmark>) {
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }

        Files.write(path, list.map(Bookmark::toTsv))
    }

    override fun delete(item: Bookmark) {
        save(list().minus(item))
    }

}

private val path = Path.of("user/bookmark/list.tsv")
