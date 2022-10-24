package util

val reservedKeywords = setOf(
    "for",
    "var",
    "function",
    "match",
    "if",
    "else",
    "true",
    "false",
    "null",
    "default",
    "return",
    "new",
    "int",
    "double",
    "boolean",
    "string",
    "char",
)

fun String.isReservedKeyword() = reservedKeywords.contains(this)