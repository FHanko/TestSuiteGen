package template

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlText
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtScriptInitializer
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

private val placeholder = Regex("""\{\{(.*?)}}""", RegexOption.DOT_MATCHES_ALL)

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

fun extractKotlin(project: Project, xmlFile: VirtualFile): String {
    val psi = PsiManager.getInstance(project).findFile(xmlFile) ?: return ""
    return PsiTreeUtil.findChildrenOfType(psi, XmlText::class.java)
        .flatMap { placeholder.findAll(it.text).toList() }
        .mapNotNull { it.groups[1]?.value }
        .joinToString("\n")
}