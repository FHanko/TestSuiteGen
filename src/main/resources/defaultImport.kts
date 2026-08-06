@Target(AnnotationTarget.PROPERTY)
annotation class TextInput

val xmlParts = mutableListOf<String>()

fun emit(value: String) {
    xmlParts.add(value)
}

var preGenerate: () -> Unit = {  }
var postGenerate: (String) -> Unit = {  }