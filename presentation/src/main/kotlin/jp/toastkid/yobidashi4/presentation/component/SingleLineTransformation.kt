/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class SingleLineTransformation : InputTransformation {

    override fun TextFieldBuffer.transformInput() {
        val text = asCharSequence()
        for (i in text.length - 1 downTo 0) {
            if (text[i] == '\n') {
                replace(i, i + 1, "")
            }
        }
    }

}