package ast.statement

import ast.expression.Expression

data class ForStatement(
    val forHead: ForHead,
    val statements: List<Statement>
)

data class ForHead(
    val variableDeclaration: VariableDeclarationStatement?,
    val forCondition: Expression?,
    val incrementExpression: Expression?
)