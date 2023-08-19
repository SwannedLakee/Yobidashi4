package jp.toastkid.yobidashi4.domain.service.slideshow

class CodeBlockBuilder {

    private var isInCodeBlock = false

    private val code = StringBuilder()
/*TODO
    fun build(): JComponent? {
        isInCodeBlock = !isInCodeBlock
        if (!isInCodeBlock && code.isNotEmpty()) {
            val codeArea = RSyntaxTextArea()
            codeArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
            //KotlinHighlighter(codeArea).highlight()
            codeArea.isEditable = false
            codeArea.isFocusable = false
            codeArea.font = codeArea.font.deriveFont(48f)
            codeArea.text = code.toString()
            code.setLength(0)
            return JScrollPane(codeArea)
        }
        return null
    }*/

    fun append(line: String) {
        code.append(if (code.isNotEmpty()) LINE_SEPARATOR else "").append(line)
    }

    fun shouldAppend(line: String): Boolean {
        return isInCodeBlock && !line.startsWith("```")
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

    }

}