package util

import ast.type.BasicType
import ast.type.Type
import gen.toyaParser

object TypeResolver {
    fun getFromTypeName(typeContext: toyaParser.TypeContext?): Type {
        if (typeContext == null) return BasicType.VOID
        val typeName = typeContext.text
        return getBuiltInType(typeName)
    }

    fun getFromValue(value: String) : Type {
        if(value.isEmpty()) return BasicType.VOID
        if(isNumeric(value)) return BasicType.INT
        return BasicType.STRING
    }

    private fun getBuiltInType(typeName: String) : BasicType {
        return BasicType.values().first { it.typeName == typeName }
    }

    private fun isNumeric(value: String) : Boolean {
        return value.all { it.isDigit() }
    }
}
