@Target(AnnotationTarget.PROPERTY)
annotation class TextInput

val xmlParts = mutableListOf<String>()

fun emit(value: String) {
    xmlParts.add(value)
}

var postGenerate: (String) -> Unit = {  }
