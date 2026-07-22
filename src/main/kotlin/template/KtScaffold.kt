package template

object KtScaffold {
    fun blockPrefix(index: Int): String {
        return if (index > 0) {
            "\nfun block${index}(): String {\n"
        } else """
            data class TextInput(text: String)
        """.trimIndent()
    }

    fun blockPostfix(index: Int): String {
        return if (index > 0) {
            "\n}\n"
        } else ""
    }
}