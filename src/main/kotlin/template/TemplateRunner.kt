package template

import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

class TemplateRunner(source: String) {
    private val host = BasicJvmScriptingHost()
    private val src = source.toScriptSource()

    private val compilationConfig = createJvmCompilationConfigurationFromTemplate<TemplateScript> {
        jvm { dependenciesFromClassContext(TemplateScript::class, wholeClasspath = true) }
    }

    suspend fun run(): TemplateScript? {
        val compiled = host.compiler(src, compilationConfig).valueOrThrow()
        val cfg = createJvmEvaluationConfigurationFromTemplate<TemplateScript> { }
        val eval = host.evaluator(compiled, cfg).valueOrThrow().returnValue
        return eval.scriptInstance as? TemplateScript
    }
}

abstract class TemplateScript(private val inputs: Map<String, String>) {
    private val parts = StringBuilder()
    fun emit(s: String) { parts.appendLine(s) }
    fun render(): String = parts.toString()
}