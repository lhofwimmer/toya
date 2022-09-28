package ast.expression

import ast.type.Type

data class VarReference(
    val varName: String,
    override val type: Type
) : Expression(type)
