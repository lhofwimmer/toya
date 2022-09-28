package ast.function

import ast.expression.Expression
import ast.expression.FunctionArgument
import ast.scope.Scope
import ast.statement.Statement
import ast.type.Type

data class Function(
    val name: String,
    val arguments: List<FunctionArgument>,
    val statements: List<Statement>,
    val scope: Scope,
    val returnType: Type
)

