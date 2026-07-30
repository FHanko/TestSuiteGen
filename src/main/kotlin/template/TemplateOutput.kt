package template

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtil
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

object TemplateOutput {
    fun openOutput(project: Project?) {
        val base = project?.guessProjectDir() ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
            val file = dir.findChild("suite.xml")
            file?.let { FileEditorManager.getInstance(project).openFile(it, true) }
        }
    }

    private fun isStringish(e: KtExpression?): Boolean = when (e) {
        is KtStringTemplateExpression -> true
        is KtDotQualifiedExpression -> isStringish(e.receiverExpression)
        else -> false
    }

    fun buildSource(file: KtFile, inputs: Map<String, String>): String {
        val edits = mutableListOf<Pair<TextRange, String>>()

        // user-supplied values replace the declared defaults
        Input.readInputFields(file).forEach { f ->
            val init = f.property.initializer ?: return@forEach
            inputs[f.name]?.let { edits += init.textRange to "\"\"\"$it\"\"\"" }
        }

        // standing strings become emit() calls
        file.script?.blockExpression?.children
            ?.filterIsInstance<KtExpression>()
            ?.filter { isStringish(it) }
            ?.forEach { edits += it.textRange to "emit(${it.text})" }

        return edits.sortedByDescending { it.first.startOffset }
            .fold(StringBuilder(file.text)) { sb, (range, text) ->
                sb.replace(range.startOffset, range.endOffset, text)
            }.toString()
    }
}