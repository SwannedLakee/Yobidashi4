/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.preview

class InternalLinkScheme {

    fun makeLink(title: String?) = "[$title]($INTERNAL_LINK_SCHEME${title?.replace(WHITE_SPACE, ESCAPED_WHITE_SPACE)})"

    fun isInternalLink(url: String): Boolean {
        return url.startsWith(INTERNAL_LINK_SCHEME)
    }

    fun extract(url: String): String {
        if (url.length <= INTERNAL_LINK_SCHEME.length || !url.startsWith(INTERNAL_LINK_SCHEME)) {
            return url
        }
        return url.substring(INTERNAL_LINK_SCHEME.length).replace(ESCAPED_WHITE_SPACE, WHITE_SPACE)
    }

    companion object {

        private const val INTERNAL_LINK_SCHEME = "https://internal/"

        private const val WHITE_SPACE = " "

        private const val ESCAPED_WHITE_SPACE = "%20"

    }

}