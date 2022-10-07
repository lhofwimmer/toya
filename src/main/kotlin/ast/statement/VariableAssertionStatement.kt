package ast.statement

import ast.expression.Expression

data class VariableAssertionStatement (
    val name: String,
    val isArray: Boolean,
    val arrayExpressionList: List<Expression>?,
    val expression: Expression
) : Statement