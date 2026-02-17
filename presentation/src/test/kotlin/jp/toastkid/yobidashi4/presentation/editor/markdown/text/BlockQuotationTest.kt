/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BlockQuotationTest {

    /**
     * Test object.
     */
    private lateinit var quotation: BlockQuotation

    /**
     * Initialize object.
     */
    @BeforeEach
    fun setUp() {
        quotation = BlockQuotation()
    }


    @ParameterizedTest
    @CsvSource(
        "tomato, > tomato",
        "1. tomato\\n2. orange\\n3. apple, > 1. tomato\\n> 2. orange\\n> 3. apple",
        "test\\n, > test\\n",
        "'', ''",
        "null, null",
        nullValues = ["null"]
    )
    fun test(input: String?, expected: String?) {
        assertEquals(expected?.replace("\\n", "\n"), quotation(input?.replace("\\n", "\n")))
    }

}