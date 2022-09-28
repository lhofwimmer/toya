package ast.expression

import ast.type.Type

data class FunctionArgument(
    val name: String,
    override val type: Type
) : Expression(type)