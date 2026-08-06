package template

import com.intellij.ide.script.IdeScriptEngineManager

object TemplateRunner {
    fun run(source: String): String {
        val engine = IdeScriptEngineManager.getInstance().getEngineByFileExtension("kts", null) ?: return "No Engine"
        val result = engine.eval(source)
        return result.toString()
    }
}