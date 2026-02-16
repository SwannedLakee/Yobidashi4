/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.TextFieldState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SingleLineTransformationTest {

    private lateinit var transformation: SingleLineTransformation

    @BeforeEach
    fun setUp() {
        transformation = SingleLineTransformation()
    }

    @Test
    fun testTransformInputRemovesAllNewLines() {
        val initialState = TextFieldState("Hello\nWorld\nNext\nLine")

        initialState.edit {
            transformation.apply { transformInput() }
        }

        assertEquals("HelloWorldNextLine", initialState.text.toString())
    }

    @ParameterizedTest
    @CsvSource(
        "'SingleLine', 'SingleLine'",
        "'\nStart', 'Start'",
        "'End\n', 'End'",
        "'\n\n', ''",
        "'A\nB\nC', 'ABC'"
    )
    fun testTransformInputPatterns(input: String, expected: String) {
        val state = TextFieldState(input)
        state.edit {
            transformation.apply { transformInput() }
        }
        assertEquals(expected, state.text.toString())
    }

}