@file:Import("import.kts")

@TextInput
val input = ""

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

