package ast.expression

import ast.type.BasicType

data class IfExpression(
    val ifCondition: Expression,
    val branch: Branch,
//    val elseIfBranches: List<ElseIfBranch>?,
    val elseBranch: Branch?
) : Expression(branch.getType())

//data class ElseIfBranch(
//    val ifCondition: Expression,
//    val branch: Branch
//)



