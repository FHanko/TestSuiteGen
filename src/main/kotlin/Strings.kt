import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

private const val BUNDLE = "messages.Strings"

internal object Strings {
    private val instance = DynamicBundle(Strings::class.java, BUNDLE)

    @JvmStatic
    fun get(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): String {
        return instance.getMessage(key, *params)
    }

    @JvmStatic
    fun lazyGet(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any?): Supplier<String> {
        return instance.getLazyMessage(key, *params)
    }
}
