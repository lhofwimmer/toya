package util

import ast.type.BasicType
import ast.type.Type
import exception.parsing.BinaryOperationTypeMismatchException
import exception.parsing.UnableToInferTypeException
import gen.toyaParser

object TypeResolver {
    fun getFromTypeName(typeContext: toyaParser.TypeContext?): Type {
        return if (typeContext == null) BasicType.VOID else BasicType.values().first { it.typeName == typeContext.text }
    }

    fun getFromTypeName(name: String?): Type {
        return if (name.isNullOrBlank()) BasicType.VOID else BasicType.values().first { it.typeName == name }
    }

    fun getFromValue(value: String?): Type {
        if (value.isNullOrEmpty()) return BasicType.VOID
        if (isBoolean(value)) return BasicType.BOOLEAN
        if (isDouble(value)) return BasicType.DOUBLE
        if (isInt(value)) return BasicType.INT
        if (isString(value)) return BasicType.STRING
        throw UnableToInferTypeException(value)
    }

    private val intRegex = Regex("""[\-+]?\d+""")
    private fun isInt(value: String): Boolean {
        return intRegex.matches(value)
    }

    private val floatRegex = Regex("""[\-+]?\d+\.\d+""")
    private fun isDouble(value: String): Boolean {
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

private val arrayTypes =
    listOf(BasicType.STRING_ARR, BasicType.INT_ARR, BasicType.BOOLEAN_ARR, BasicType.DOUBLE_ARR)

fun Type.isArrayType() = arrayTypes.contains(this)

fun Type.checkTypeMatch(rhs: Type) {
    if (this != rhs) throw BinaryOperationTypeMismatchException(this, rhs)
}

fun <T> Type.handleTypeGroups(
    i: () -> T,
    d: () -> T,
    a: () -> T,
    z: () -> T
): T {
    return when (this) {
        BasicType.INT -> i()
        BasicType.DOUBLE -> d()
        BasicType.STRING -> a()
        BasicType.BOOLEAN -> z()
        else -> throw NotImplementedError("handling for type '${this.typeName}' not implemented")
    }
}

fun Type.toArrayType() : BasicType {
    return when (this) {
        BasicType.INT -> BasicType.INT_ARR
        BasicType.DOUBLE -> BasicType.DOUBLE_ARR
        BasicType.STRING -> BasicType.STRING_ARR
        BasicType.BOOLEAN -> BasicType.BOOLEAN_ARR
        else -> throw NotImplementedError("conversion to array for type '${this.typeName}' not implemented")
    }
}

fun Type.toSingleType() : BasicType {
    return when (this) {
        BasicType.INT_ARR -> BasicType.INT
        BasicType.DOUBLE_ARR -> BasicType.DOUBLE
        BasicType.STRING_ARR -> BasicType.STRING
        BasicType.BOOLEAN_ARR -> BasicType.BOOLEAN
        else -> throw NotImplementedError("conversion from array for type '${this.typeName}' not implemented")
    }
}

fun <T> Type.handleTypeArrays(
    ia: () -> T,
    da: () -> T,
    aa: () -> T,
    ba: () -> T
): T {
    return when (this) {
        BasicType.INT_ARR -> ia()
        BasicType.DOUBLE_ARR -> da()
        BasicType.STRING_ARR -> aa()
        BasicType.BOOLEAN_ARR -> ba()
        else -> throw NotImplementedError("handling for type '${this.typeName}' not implemented")
    }
}