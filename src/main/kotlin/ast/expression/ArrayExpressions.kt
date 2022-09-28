package ast.expression

import ast.type.Type

data class ArrayDeclaration(
    val dimensions: List<ArrayDimension>,
    override val type: Type
): Expression(type)

data class ArrayDimension(
    val size: Expression
)

data class ArrayAccess(
    val name: String,
    override val type: Type,
    val dimensions: List<ArrayDimension>
): Expression(type)