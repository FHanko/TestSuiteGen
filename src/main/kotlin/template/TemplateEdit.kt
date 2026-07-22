package template

import EntryPointButton
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

object TemplateEdit {
    private val DEFAULT_TEMPLATE: String by lazy {
        EntryPointButton::class.java.getResourceAsStream("/defaultTemplate.xml")
            ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
    }

    fun getOrCreateTemplate(project: Project): VirtualFile? {
        val base = project.guessProjectDir() ?: return null
        return WriteCommandAction.runWriteCommandAction<VirtualFile?>(project) {
            val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
            dir.findChild("template.xml")
                ?: dir.createChildData(this, "template.xml").also {
                    VfsUtil.saveText(it, DEFAULT_TEMPLATE)
                }
        }
    }

    fun openProjectTemplate(project: Project?) {
        val proj = project ?: return
        val file = getOrCreateTemplate(proj) ?: return
        FileEditorManager.getInstance(proj).openFile(file, true)
    }
}