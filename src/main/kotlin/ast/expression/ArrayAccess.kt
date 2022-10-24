package ast.expression

import ast.type.Type

data class ArrayAccess(
    val name: String,
    override val type: Type,
    val location: Expression
): Expression(type)