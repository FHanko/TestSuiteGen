package com.github.fhanko

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader

class GenerateSuitePanel : AnAction(
    "Generate TestNG Suite",
    "Generate a testng.xml from a template",
    IconLoader.getIcon("/icons/debug.svg", GenerateSuitePanel::class.java)
), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val group = DefaultActionGroup().apply {
            add(button("Edit Template")      {
                TemplateEdit.openProjectTemplate(e.project)
            })
            add(button("View Output")    { /* ... */ })
            add(button("Generate")   { /* ... */ })
        }

        val popup = JBPopupFactory.getInstance().createActionGroupPopup(
            "Generate Suite",
            group,
        e.dataContext,
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
            true
        )

        // anchor under toolbar button
        val source = e.inputEvent?.component
        if (source != null) popup.showUnderneathOf(source)
        else popup.showInBestPositionFor(e.dataContext)
    }

    override fun update(e: AnActionEvent) { e.presentation.isEnabledAndVisible = e.project != null }
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    private fun button(text: String, onClick: (AnActionEvent) -> Unit) =
        object : AnAction(text), DumbAware {
            override fun actionPerformed(e: AnActionEvent) = onClick(e)
        }
}