import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import kotlin.jvm.java

enum class InputType() {
    TextInput
}

class Input(val name: String, val args: List<String>) {
    companion object {
        fun readInputFields(project: Project, kotlinSource: String): List<Input> {
            val ktFile = PsiFileFactory.getInstance(project)
                .createFileFromText("template.kts", KotlinFileType.INSTANCE, kotlinSource) as KtFile

            return PsiTreeUtil.findChildrenOfType(ktFile, KtProperty::class.java).mapNotNull { prop ->
                val call = prop.initializer as? KtCallExpression ?: return@mapNotNull null
                if (!InputType.entries.map { it.name }.contains(call.calleeExpression?.text)) return@mapNotNull null
                val args = call.valueArguments.map { it.getArgumentExpression()?.text.orEmpty() }
                Input(prop.name ?: return@mapNotNull null, args)
            }
        }
    }
}