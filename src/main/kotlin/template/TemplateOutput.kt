package template

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil

object TemplateOutput {
    fun openOutput(project: Project?) {
        val base = project?.guessProjectDir() ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
            val file = dir.findChild("suite.xml")
            file?.let { FileEditorManager.getInstance(project).openFile(it, true) }
        }
    }
}