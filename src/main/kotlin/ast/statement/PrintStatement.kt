package ast.statement

import ast.expression.Expression

data class PrintStatement(
    val expression: Expression
) : Statement
