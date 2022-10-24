package ast.statement

import ast.expression.Expression
import ast.scope.LocalVariable

data class VariableAssertionStatement (
    val localVariable: LocalVariable,
    val isArray: Boolean,
    val arrayExpression: Expression?,
    val expression: Expression
) : Statement