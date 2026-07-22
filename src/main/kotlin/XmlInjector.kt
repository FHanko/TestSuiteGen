package com.github.fhanko

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlText
import org.jetbrains.kotlin.idea.KotlinLanguage

class XmlInjector : MultiHostInjector {
    private val placeholder = Regex("""\{\{(.*?)}}""", RegexOption.DOT_MATCHES_ALL)

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is XmlText || context !is PsiLanguageInjectionHost) return
        val vFile = context.containingFile?.virtualFile ?: return
        if (!vFile.path.contains("/.testsuitegen/")) return

        val text = context.text

        val matches = placeholder.findAll(text).toList()
        if (matches.isEmpty()) return

        registrar.startInjecting(KotlinLanguage.INSTANCE)
        for (m in matches) {
            val inner = m.groups[1] ?: continue
            registrar.addPlace(
                null,
                null,
                context,
                TextRange(inner.range.first, inner.range.last + 1)
            )
        }
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(XmlText::class.java)
}