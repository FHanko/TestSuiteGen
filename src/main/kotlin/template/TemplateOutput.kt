package template

import andel.text.Text
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
import kotlin.collections.plusAssign
import kotlin.collections.sortedByDescending

object TemplateOutput {
    fun openOutput(project: Project?) {
        val base = project?.guessProjectDir() ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
            val file = dir.findChild("suite.xml")
            file?.let { FileEditorManager.getInstance(project).openFile(it, true) }
        }
    }

    private fun isEmittable(e: KtExpression?): Boolean = when (e) {
        is KtStringTemplateExpression -> true
        is KtDotQualifiedExpression -> isEmittable(e.receiverExpression)
        else -> false
    }

    fun fileWithReplacements(file: KtFile, replacements: List<Pair<TextRange, String>>): String {
        return replacements
            .sortedByDescending { it.first.startOffset }
            .fold(StringBuilder(file.text)) { sb, (range, text) ->
                sb.replace(range.startOffset, range.endOffset, text)
            }.toString()
    }

    fun placeInputs(file: KtFile, inputs: Map<String, String>) {
        val replacements = Input.readInputFields(file).mapNotNull { f ->
            val init = f.property.initializer ?: return@mapNotNull null
            inputs[f.name]?.let { text ->
                return@mapNotNull init.textRange to when (init) {
                    is KtStringTemplateExpression -> "\"${text.replace("\"", "'")}\""
                    else -> text
                }
            }
        }

        fileWithReplacements(file, replacements).let { Template.writeFile(file.project, file.virtualFile, it) }
    }

    fun buildSource(file: KtFile, inputs: Map<String, String>): String {
        val edits = mutableListOf<Pair<TextRange, String>>()

        file.script?.blockExpression?.children
            ?.filterIsInstance<KtExpression>()
            ?.filter { isEmittable(it) }
            ?.forEach { edits += it.textRange to "emit(${it.text})" }

        return edits.sortedByDescending { it.first.startOffset }
            .fold(StringBuilder(file.text)) { sb, (range, text) ->
                sb.replace(range.startOffset, range.endOffset, text)
            }.toString()
    }
}