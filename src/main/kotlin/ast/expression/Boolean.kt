package ast.expression

import ast.type.BasicType

sealed class BooleanExpression(
    open val leftExpression: Expression, open val rightExpression: Expression
) : Expression(BasicType.BOOLEAN)

data class AndExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : BooleanExpression(leftExpression, rightExpression)

data class OrExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : BooleanExpression(leftExpression, rightExpression)


sealed class ComparativeExpression(
    open val leftExpression: Expression, open val rightExpression: Expression
) : Expression(BasicType.BOOLEAN)

data class NotExpression(
    val expression: Expression
) : Expression(BasicType.BOOLEAN)

data class GreaterThanExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class GreaterEqualExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class EqualExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class LessEqualExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class LessThanExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class NotEqualExpression(
    override val rightExpression: Expression, override val leftExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)