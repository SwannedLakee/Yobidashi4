package jp.toastkid.yobidashi4.domain.model.web.ad

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

class AdHosts(private val adHosts: Set<String>) {

    fun contains(url: String?): Boolean {
        return url != null && adHosts.any { url.contains(it) }
    }

    companion object {

        fun make() =
            AdHosts(
                loadInputStream()?.use { stream ->
                    return@use BufferedReader(InputStreamReader(stream)).use { reader ->
                        reader.lines().collect(Collectors.toUnmodifiableSet())
                    }
                } ?: emptySet()
            )

        fun loadInputStream(): InputStream? =
            AdHosts::class.java.classLoader.getResourceAsStream("web/ad_hosts.txt")

    }

}