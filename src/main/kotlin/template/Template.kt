package template

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager

object Template {
    const val TEMPLATE_DIR = ".testsuitegen"

    fun readFile(path: String): String {
        return EntryPointButton::class.java.getResourceAsStream(path)
            ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
    }

    fun writeFile(project: Project, vFile: VirtualFile, text: String) {
        val docManager = FileDocumentManager.getInstance()
        val doc = docManager.getDocument(vFile) ?: return
        WriteCommandAction.runWriteCommandAction(project, "Write File", null, {
            doc.setText(StringUtil.convertLineSeparators(text))
            PsiDocumentManager.getInstance(project).commitDocument(doc)
        })
        docManager.saveDocument(doc)
    }
}