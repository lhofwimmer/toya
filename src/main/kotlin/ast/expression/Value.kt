package ast.expression

import ast.type.Type

data class Value(
    val value: String,
    override val type: Type
) : Expression(type)
