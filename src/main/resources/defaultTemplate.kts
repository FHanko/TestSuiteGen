@file:Import("import.kts")

val input = TextInput("")

"""
<?xml version = "1.0" encoding = "UTF-8"?>
    <suite name = "Suite1">
    <test name = "Test1">
        <classes>
            <class name = "$input"/>
        </classes>
    </test>
</suite>
"""

