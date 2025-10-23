package jp.toastkid.yobidashi4.domain.model.browser

import java.awt.Component

interface WebViewPool {
    fun component(id: String, initialUrl: String): Component
    fun dispose(id: String)
    fun disposeAll()
    fun find(id: String, text: String, forward: Boolean)

    fun clearFind(id: String)

    fun reload(id: String)

    fun switchDevTools(id: String)

    fun findId(browser: Any): String?

}