import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.components.JBTextField
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.plainContent
import javax.swing.JComponent

enum class InputType {
    TextInput
}

abstract class Input(val type: InputType, val name: String) {
    abstract val component: JComponent

    companion object {
        fun readInputFields(project: Project, ktFile: VirtualFile): List<Input> {
            val psi = PsiManager.getInstance(project).findFile(ktFile) as? KtFile ?: return emptyList()
            val properties = psi.script?.blockExpression?.children?.filterIsInstance<KtProperty>() ?: return emptyList()

            return properties.mapNotNull { prop ->
                val type = InputType.entries.find { t ->
                    prop.annotationEntries.first { it.shortName?.asString() == t.name } != null
                } ?: return@mapNotNull null
                val value = literalValue(prop.initializer)
                val name = prop.name ?: return@mapNotNull null
                when (type) {
                    InputType.TextInput -> {
                        TextInput(name, value ?: "")
                    }
                }
            }
        }

        private fun literalValue(expr: KtExpression?): String? = when (expr) {
            is KtStringTemplateExpression -> if (expr.hasInterpolation()) null else expr.plainContent
            is KtConstantExpression -> expr.text
            else -> null
        }
    }
}

class TextInput(name: String, text: String): Input(InputType.TextInput, name) {
    override val component = JBTextField(text)

    override fun toString(): String {
        return component.text
    }
}