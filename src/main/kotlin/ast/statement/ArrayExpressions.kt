package ast.expression

import ast.type.Type

data class ArrayDeclarationStatement(
    val name: String,
    val size: Expression,
    override val type: Type
): Expression(type)