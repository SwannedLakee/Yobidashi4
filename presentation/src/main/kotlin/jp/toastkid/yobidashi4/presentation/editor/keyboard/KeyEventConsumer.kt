package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.TableFormConverter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ToHalfWidth
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min

class KeyEventConsumer(
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm : MainViewModel by inject() }.vm,
    private val controlAndLeftBracketCase: ControlAndLeftBracketCase = ControlAndLeftBracketCase(),
    private val selectedTextConversion: SelectedTextConversion = SelectedTextConversion(),
    private val searchUrlFactory: SearchUrlFactory = SearchUrlFactory(),
    private val toHalfWidth: ToHalfWidth = ToHalfWidth(),
    private val expressionTextCalculatorService: ExpressionTextCalculatorService = ExpressionTextCalculatorService(),
    private val blockQuotation: BlockQuotation = BlockQuotation()
) {

    operator fun invoke(
        it: KeyEvent,
        content: TextFieldValue,
        lastParagraph: MultiParagraph?,
        setNewContent: (TextFieldValue) -> Unit
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }

        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return when {
            it.isCtrlPressed && it.key == Key.D -> {
                val selected = content.getSelectedText()
                if (selected.isNotEmpty()) {
                    val newText = StringBuilder(content.text)
                        .insert(selectionEndIndex, selected)
                        .toString()
                    setNewContent(content.copy(newText))
                    return true
                }

                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val safeEnd = min(content.text.length, lineEnd)
                val newText = StringBuilder(content.text)
                    .insert(safeEnd, "\n${content.text.substring(lineStart, safeEnd)}")
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        content.selection,
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Minus -> {
                val selected = content.getSelectedText()
                if (selected.isEmpty()) {
                    return false
                }

                val converted = ListHeadAdder().invoke(selected.text, "-") ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.One -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = NumberedListHeadAdder().invoke(selected) ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Zero -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = ListHeadAdder().invoke(selected, "- [ ]") ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Four -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                setNewContent(content.copy(selection = TextRange(lineStart)))
                true
            }
            it.isCtrlPressed && it.key == Key.E -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                setNewContent(content.copy(selection = TextRange(lineEnd)))
                true
            }
            it.isCtrlPressed && it.key == Key.Comma -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = CommaInserter().invoke(selected) ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.T -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val tableString = TableFormConverter().invoke(selected)
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        tableString
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + tableString.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.U -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::switchCase, setNewContent)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.H -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, toHalfWidth::invoke, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.B -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::bold, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.I -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                    italic(it)
                }, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.Two -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                    "\"$it\""
                }, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.Eight -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                    "($it)"
                }, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.LeftBracket -> {
                return controlAndLeftBracketCase.invoke(content, selectionStartIndex, setNewContent)
            }
            it.isCtrlPressed && it.key == Key.RightBracket -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                    "「$it」"
                }, setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.At -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                    "```$it```"
                }, setNewContent)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.C -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, expressionTextCalculatorService::invoke, setNewContent)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.N -> {
                (mainViewModel.currentTab() as? EditorTab)?.switchEditable()
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                mainViewModel.openUrl(searchUrlFactory(selected), false)
                true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val url = searchUrlFactory(selected)
                mainViewModel.browseUri(url)
                true
            }
            it.isCtrlPressed && it.key == Key.O -> {
                val tab = mainViewModel.currentTab() as? EditorTab ?: return false
                mainViewModel.openFile(tab.path)
                true
            }
            it.isCtrlPressed && it.key == Key.Q -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isNotEmpty()) {
                    selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                        blockQuotation.invoke(it)
                    }, setNewContent)
                    return true
                }

                val clipped = ClipboardFetcher().invoke()
                if (!clipped.isNullOrEmpty()) {
                    val decoratedLink = blockQuotation.invoke(clipped) ?: return false
                    val newText = StringBuilder(content.text)
                        .insert(
                            selectionStartIndex,
                            decoratedLink
                        )
                        .toString()
                    setNewContent(
                        TextFieldValue(
                            newText,
                            TextRange(selectionStartIndex + decoratedLink.length + 1),
                            content.composition
                        )
                    )
                    return true
                }

                true
            }
            it.isCtrlPressed && it.key == Key.J -> {
                val index = content.text.indexOf("\n", selectionStartIndex)
                if (index == -1) {
                    return false
                }

                setNewContent(content.copy(content.text.removeRange(index, index + 1)))
                true
            }
            it.isCtrlPressed && it.key == Key.L -> {
                LinkPasteCase()
                    .invoke(content, selectionStartIndex, selectionEndIndex, selectedTextConversion, setNewContent)
            }
            else -> false
        }
    }

    private fun italic(it: String) = "***$it***"

    private fun bold(it: String) = "**$it**"

    private fun switchCase(it: String) = if (it.toCharArray()[0].isUpperCase()) it.lowercase() else it.uppercase()

}