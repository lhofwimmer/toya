package ast.statement

import ast.expression.Expression
import ast.scope.Scope

data class ForStatement(
    val forHead: ForHead,
    val statements: List<Statement>,
    val scope: Scope
) : Statement

data class ForHead(
    val variableDeclaration: VariableDeclarationStatement?,
    val forCondition: Expression?,
    val incrementStatement: Statement?
)