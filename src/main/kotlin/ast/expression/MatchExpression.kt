package ast.expression

data class MatchExpression(
    val matchHead: Expression,
    val branches: List<MatchBranch>,
    val defaultBranch: Branch
) : Expression(defaultBranch.getType())

data class MatchBranch(
    val value: Value,
    val branch: Branch
)