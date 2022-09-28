package ast.statement

import ast.expression.Expression

data class VariableDeclarationStatement(
    val name: String,
    val expression: Expression
) : Statement