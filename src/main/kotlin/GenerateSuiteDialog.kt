import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

class GenerateSuiteDialog(
    private val project: Project,
    private val templateFile: VirtualFile,
    //fields: List<InputFieldDecl>
) : DialogWrapper(project) {

    // one text box per parsed InputField, pre-filled with its default arg
    //private val editors = fields.associateWith { JBTextField(it.args.firstOrNull().orEmpty()) }
    override fun createActions(): Array<Action> = arrayOf(okAction)

    init {
        title = "Generate Suite"
        setOKButtonText("Generate")     // OK button *is* Generate
        init()
    }

    override fun createCenterPanel(): JComponent {
        val builder = FormBuilder.createFormBuilder()
        //editors.forEach { (field, editor) -> builder.addLabeledComponent("${field.name}:", editor) }
        return builder.panel.apply { preferredSize = Dimension(420, preferredSize.height) }
    }

    // Edit Template / View Output live here, beside OK & Cancel
    override fun createLeftSideActions(): Array<Action> = arrayOf(
        object : DialogWrapperAction("Edit Template") {
            override fun doAction(e: ActionEvent?) {
                FileEditorManager.getInstance(project).openFile(templateFile, true)
                close(CANCEL_EXIT_CODE)   // close so the user can edit; reopen to re-parse
            }
        },
        object : DialogWrapperAction("View Output") {
            override fun doAction(e: ActionEvent?) {
                /* open/generate output */
            }
        }
    )

    /** The filled-in values, read after the dialog returns OK. */
    //val values: Map<String, String>
        //get() = editors.entries.associate { (f, e) -> f.name to e.text }
}