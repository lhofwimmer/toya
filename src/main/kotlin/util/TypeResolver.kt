package util

import ast.type.BasicType
import ast.type.Type
import exception.parsing.UnableToInferTypeException
import gen.toyaParser

object TypeResolver {
    fun getFromTypeName(typeContext: toyaParser.TypeContext?): Type {
        return if (typeContext == null) BasicType.VOID else BasicType.values().first { it.typeName == typeContext.text }
    }

    fun getFromValue(value: String?): Type {
        if (value.isNullOrEmpty()) return BasicType.VOID
        if (isBoolean(value)) return BasicType.BOOLEAN
        if (isFloat(value)) return BasicType.FLOAT
        if (isInt(value)) return BasicType.INT
        if (isString(value)) return BasicType.STRING
        throw UnableToInferTypeException(value)
    }

    private val intRegex = Regex("""[\-+]?\d+""")
    private fun isInt(value: String): Boolean {
        return intRegex.matches(value)
    }

    private val floatRegex = Regex("""[\-+]?\d+\.\d+""")
    private fun isFloat(value: String): Boolean {
        return floatRegex.matches(value)
    }

    private val stringRegex = Regex("""^"[^"]*"$""")
    private fun isString(value: String): Boolean {
        return stringRegex.matches(value)
    }

    private fun isBoolean(value: String): Boolean {
        return value == "false" || value == "true"
    }
}
