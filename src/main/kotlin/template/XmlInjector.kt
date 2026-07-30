package template

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtScriptInitializer
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

private val KtStringTemplateExpression.isUnusedStatement: Boolean
    get() = parent is KtBlockExpression || parent is KtScriptInitializer

class XmlInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is KtStringTemplateExpression) return
        if (!context.isUnusedStatement) return
        val vFile = context.containingFile.virtualFile ?: return
        if (!vFile.path.contains("/.testsuitegen/")) return

        val literalEntries = context.entries.filterIsInstance<KtLiteralStringTemplateEntry>()
        if (literalEntries.isEmpty()) return

        registrar.startInjecting(XMLLanguage.INSTANCE)
        literalEntries.forEach { entry ->
            val start = entry.startOffsetInParent
            registrar.addPlace(null, null, context, TextRange(start, start + entry.textLength))
        }
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(KtStringTemplateExpression::class.java)
}