import com.github.fhanko.template.TemplateEdit
import com.github.fhanko.template.extractKotlin
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.IconLoader

class EntryPointButton : AnAction(
    "Generate TestNG Suite",
    "Generate a testng.xml from a template",
    IconLoader.getIcon("/icons/debug.svg", EntryPointButton::class.java)
), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val templateFile = TemplateEdit.getOrCreateTemplate(project) ?: return
        val kotlinSource = extractKotlin(project, templateFile)
        val fields = Input.readInputFields(project, kotlinSource)

        val dialog = GenerateSuiteDialog(project, templateFile)
        if (dialog.showAndGet()) {                 // true only when Generate/OK was clicked
            //val values = dialog.values             // Map<fieldName, enteredValue>
            // execute template blocks with `values`, splice into XML, write output
        }
    }

    override fun update(e: AnActionEvent) { e.presentation.isEnabledAndVisible = e.project != null }
    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}