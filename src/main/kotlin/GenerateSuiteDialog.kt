import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

class GenerateSuiteDialog(
    private val project: Project,
    private val templateFile: VirtualFile,
    val fields: List<Input>
) : DialogWrapper(project) {
    override fun createActions(): Array<Action> = arrayOf(okAction)

    init {
        title = "Generate Suite"
        setOKButtonText("Generate")     // OK button *is* Generate
        init()
    }

    override fun createCenterPanel(): JComponent {
        val builder = FormBuilder.createFormBuilder()
        fields.forEach {
            when (it.type) {
                InputType.TextInput -> {
                    builder.addLabeledComponent("${it.name}:", it.component)
                }
            }
        }

        return builder.panel.apply { preferredSize = Dimension(500, preferredSize.height) }
    }

    // Edit Template / View Output live here, beside OK & Cancel
    override fun createLeftSideActions(): Array<Action> = arrayOf(
        object : DialogWrapperAction("Edit Template") {
            override fun doAction(e: ActionEvent?) {
                FileEditorManager.getInstance(project).openFile(templateFile, true)
                close(CANCEL_EXIT_CODE)
            }
        },
        object : DialogWrapperAction("View Output") {
            override fun doAction(e: ActionEvent?) {

            }
        }
    )
}