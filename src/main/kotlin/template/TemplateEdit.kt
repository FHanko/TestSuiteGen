package com.github.fhanko.template

import com.github.fhanko.GenerateSuitePanel
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil

object TemplateEdit {
    private val DEFAULT_TEMPLATE: String by lazy {
        GenerateSuitePanel::class.java.getResourceAsStream("/defaultTemplate.xml")
            ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
    }

    fun openProjectTemplate(project: Project?) {
        val base = project?.guessProjectDir() ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val dir = VfsUtil.createDirectoryIfMissing(base, ".testsuitegen")
            val file = dir.findChild("template.xml")
                ?: dir.createChildData(this, "template.xml").also {
                    VfsUtil.saveText(it, DEFAULT_TEMPLATE)
                }
            FileEditorManager.getInstance(project).openFile(file, true)
        }
    }
}