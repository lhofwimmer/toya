package ast.expression

import ast.type.BasicType

sealed class BooleanExpression(
    open val leftExpression: Expression, open val rightExpression: Expression
) : Expression(BasicType.BOOLEAN)

data class AndExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : BooleanExpression(leftExpression, rightExpression)

data class OrExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : BooleanExpression(leftExpression, rightExpression)


sealed class ComparativeExpression(
    open val leftExpression: Expression, open val rightExpression: Expression
) : Expression(BasicType.BOOLEAN)

data class NotExpression(
    val expression: Expression
) : Expression(BasicType.BOOLEAN)

data class GreaterThanExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class GreaterEqualExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class EqualExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class LessEqualExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class LessThanExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)

data class NotEqualExpression(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ComparativeExpression(leftExpression, rightExpression)