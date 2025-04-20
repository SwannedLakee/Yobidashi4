package jp.toastkid.yobidashi4.domain.service.markdown

import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLineBuilder
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.service.slideshow.CodeBlockBuilder
import jp.toastkid.yobidashi4.domain.service.slideshow.ImageExtractor
import jp.toastkid.yobidashi4.domain.service.slideshow.TableBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

class MarkdownParser {

    /** Table builder.  */
    private val tableBuilder = TableBuilder()

    private val codeBlockBuilder = CodeBlockBuilder()

    private val imageExtractor = ImageExtractor()

    private val stringBuilder = StringBuilder()

    private val listLineBuilder = ListLineBuilder()

    private val orderedListPrefixPattern = "^[0-9]+\\.".toRegex()

    private val horizontalRulePattern = "^-{3,}$".toRegex()

    private val codeFencePattern = Pattern.compile("```(.+?)```", Pattern.DOTALL)

    operator fun invoke(path: Path): Markdown {
        return invoke(Files.lines(path), path.nameWithoutExtension)
    }

    operator fun invoke(content: String, title: String): Markdown {
        return invoke(content.split("\n").stream(), title)
    }

    /**
     * Convert to Slides.
     * @return List&lt;Slide&gt;
     */
    private fun invoke(stream: Stream<String>, title: String): Markdown {
        val markdown = Markdown(title)
        stream.forEach { line ->
            if (line.startsWith("#")) {
                markdown.add(
                    TextBlock(
                        line.substring(line.indexOf(" ") + 1),
                        level = line.split(" ")[0].length
                    )
                )
                return@forEach
            }

            if (line.startsWith("![")) {
                markdown.addAll(imageExtractor.invoke(line))
                return@forEach
            }

            if (line.startsWith("> ")) {
                markdown.add(TextBlock(line.substring(2), quote = true))
                return@forEach
            }
            // Adding code block.
            if (line.startsWith("```") && !codeFencePattern.matcher(line).find()) {
                if (codeBlockBuilder.inCodeBlock()) {
                    codeBlockBuilder.build().let {
                        markdown.add(it)
                        codeBlockBuilder.initialize()
                    }
                    return@forEach
                }

                codeBlockBuilder.startCodeBlock()
                val index = line.indexOf(":")
                val lastIndex = if (index == -1) line.length else index
                codeBlockBuilder.setCodeFormat(line.substring(3, lastIndex))
                return@forEach
            }
            if (codeBlockBuilder.shouldAppend(line)) {
                codeBlockBuilder.append(line)
                return@forEach
            }

            if (TableBuilder.isTableStart(line)) {
                if (!tableBuilder.active()) {
                    tableBuilder.setActive()
                }

                if (TableBuilder.shouldIgnoreLine(line)) {
                    return@forEach
                }

                if (!tableBuilder.hasColumns()) {
                    tableBuilder.setColumns(line)
                    return@forEach
                }

                tableBuilder.addTableLines(line)
                return@forEach
            }

            if (tableBuilder.active()) {
                markdown.add(tableBuilder.build())
                tableBuilder.setInactive()
                tableBuilder.clear()
            }

            if (line.startsWith("- [ ] ") || line.startsWith("- [x] ")) {
                listLineBuilder.setTaskList()
                listLineBuilder.add(line)
                return@forEach
            }

            if (line.startsWith("- ")) {
                listLineBuilder.add(line)
                return@forEach
            }

            if (orderedListPrefixPattern.containsMatchIn(line)) {
                listLineBuilder.setOrdered()
                listLineBuilder.add(line)
                return@forEach
            }

            if (horizontalRulePattern.containsMatchIn(line)) {
                markdown.add(HorizontalRule())
                return@forEach
            }

            if (listLineBuilder.isNotEmpty()) {
                markdown.add(listLineBuilder.build())
                listLineBuilder.clear()
                return@forEach
            }

            // Not code.
            if (line.isNotEmpty()) {
                stringBuilder.append(line)
                return@forEach
            }

            if (stringBuilder.isNotEmpty()) {
                markdown.add(TextBlock(stringBuilder.toString()))
                stringBuilder.setLength(0)
                return@forEach
            }
        }

        if (stringBuilder.isNotEmpty()) {
            markdown.add(TextBlock(stringBuilder.toString()))
        }
        if (listLineBuilder.isNotEmpty()) {
            markdown.add(listLineBuilder.build())
            listLineBuilder.clear()
        }
        if (tableBuilder.active()) {
            markdown.add(tableBuilder.build())
            tableBuilder.setInactive()
            tableBuilder.clear()
        }

        return markdown
    }

}