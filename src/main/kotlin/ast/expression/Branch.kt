package ast.expression

import ast.statement.Statement
import ast.type.BasicType

// Sum type 2: Expression or Statement

/**
 * Sum type used to both represent `{ (statement|expression)* }` and `expression`
 * Used in MatchExpression and IfExpression for example
 */

sealed interface Branch

data class BranchStatement(
    val statement: List<Statement>
) : Branch

data class BranchExpression(
    val expression: Expression
): Branch

fun Branch.getType() = if(this is BranchExpression) this.expression.type else BasicType.VOID