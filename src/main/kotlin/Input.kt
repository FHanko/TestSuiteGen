import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.JBTextField
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import javax.swing.JComponent
import kotlin.jvm.java

enum class InputType() {
    TextInput
}

abstract class Input(val type: InputType, val name: String) {
    abstract val component: () -> JComponent

    companion object {
        fun readInputFields(project: Project, kotlinSource: String): List<Input> {
            val ktFile = PsiFileFactory.getInstance(project)
                .createFileFromText("template.kts", KotlinFileType.INSTANCE, kotlinSource) as KtFile

            return PsiTreeUtil.findChildrenOfType(ktFile, KtProperty::class.java).mapNotNull { prop ->
                val call = prop.initializer as? KtCallExpression ?: return@mapNotNull null
                val type = InputType.entries.find { it.name == call.calleeExpression?.text } ?: return@mapNotNull null
                val args = call.valueArguments.map { it.getArgumentExpression()?.text.orEmpty() }
                val name = prop.name ?: return@mapNotNull null
                when (type) {
                    InputType.TextInput -> {
                        TextInput(name, args.getOrNull(0) ?: "")
                    }
                }
            }
        }
    }
}

class TextInput(name: String, val text: String): Input(InputType.TextInput, name) {
    override val component = {
        JBTextField(text)
    }
}