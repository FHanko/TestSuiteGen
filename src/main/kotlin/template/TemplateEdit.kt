package template

import EntryPointButton
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

object TemplateEdit {
    private val defaultFiles = listOf("/defaultTemplate.kts" to "template.main.kts", "/defaultImport.kts" to "import.kts")

    private fun readFile(path: String): String {
        return EntryPointButton::class.java.getResourceAsStream(path)
            ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
    }

    fun getOrCreateTemplate(project: Project): VirtualFile? {
        val base = project.guessProjectDir() ?: return null
        val files = defaultFiles.map { paths ->
            WriteCommandAction.runWriteCommandAction<VirtualFile?>(project) {
                val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
                return@runWriteCommandAction dir.findChild(paths.second)
                    ?: dir.createChildData(this, paths.second).also {
                        VfsUtil.saveText(it, readFile(paths.first))
                    }
            }
        }
        return files.firstOrNull()
    }

    fun openProjectTemplate(project: Project?) {
        val proj = project ?: return
        val file = getOrCreateTemplate(proj) ?: return
        FileEditorManager.getInstance(proj).openFile(file, true)
    }
}