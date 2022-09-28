package ast.statement

import ast.expression.Expression

data class ReturnStatement(
    val expression: Expression
) : Statement