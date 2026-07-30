import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.IconLoader
import template.TemplateEdit

class EntryPointButton : AnAction(
    "Generate TestNG Suite",
    "Generate a testng.xml from a template",
    IconLoader.getIcon("/icons/debug.svg", EntryPointButton::class.java)
), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val templateFile = TemplateEdit.getOrCreateTemplate(project) ?: return
        val fields = Input.readInputFields(project, templateFile)

        val dialog = GenerateSuiteDialog(project, templateFile, fields)
        if (dialog.showAndGet()) {
            dialog.fields.forEach {
                println(it)
            }
        }
    }

    override fun update(e: AnActionEvent) { e.presentation.isEnabledAndVisible = e.project != null }
    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}