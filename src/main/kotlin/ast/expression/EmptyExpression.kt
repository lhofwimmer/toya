package ast.expression

import ast.type.Type

data class EmptyExpression(
    override val type: Type
) : Expression(type)