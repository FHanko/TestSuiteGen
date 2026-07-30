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

abstract class Input(val type: InputType, val name: String, val property: KtProperty) {
    abstract val component: JComponent

    companion object {
        fun readInputFields(ktFile: KtFile): List<Input> {
            val properties = ktFile.script?.blockExpression?.children?.filterIsInstance<KtProperty>() ?: return emptyList()

            return properties.mapNotNull { prop ->
                val type = InputType.entries.find { t ->
                    prop.annotationEntries.first { it.shortName?.asString() == t.name } != null
                } ?: return@mapNotNull null
                val value = literalValue(prop.initializer)
                val name = prop.name ?: return@mapNotNull null
                when (type) {
                    InputType.TextInput -> {
                        TextInput(name, value ?: "", prop)
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

class TextInput(name: String, text: String, property: KtProperty): Input(InputType.TextInput, name, property) {
    override val component = JBTextField(text)

    override fun toString(): String {
        return component.text
    }
}