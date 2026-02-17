/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.setting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ColorDecoderServiceTest {

    private lateinit var colorDecoderService: ColorDecoderService

    @BeforeEach
    fun setUp() {
        colorDecoderService = ColorDecoderService()
    }

    @ParameterizedTest
    @CsvSource(
        "null",
        "''",
        "' '",
        nullValues = ["null"]
    )
    fun testReturnNull(input: String?) {
        assertNull(colorDecoderService.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "#000099,0,0,153,255",
        "#99990099,153,0,153,153",
        "99990099,153,0,153,153"
    )
    fun test(colorCode: String, red: Int, green: Int, blue: Int, alpha: Int) {
        val color = colorDecoderService.invoke(colorCode)
                ?: fail("This case doesn't allow null.")

        assertEquals(red, color.red)
        assertEquals(green, color.green)
        assertEquals(blue, color.blue)
        assertEquals(alpha, color.alpha)
    }

}