@file:Import("import.kts")

@TextInput
val files = "Test1, Test2, Test3"

"""<?xml version = "1.0" encoding = "UTF-8"?>
    <suite name = "Suite1">
    <test name = "Test1">
        <classes>
            ${files.split(',').joinToString("\n") { """<class> ${it}</class>""" }}
        </classes>
    </test>
</suite>"""