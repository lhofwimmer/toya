package ast.expression

import ast.type.Type

sealed class ArithmeticOperation(
    open val leftExpression: Expression, open val rightExpression: Expression, override val type: Type
) : Expression(type)

data class Addition(
    override val leftExpression: Expression, override val rightExpression: Expression,
) : ArithmeticOperation(leftExpression, rightExpression, leftExpression.type)

data class Multiplication(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ArithmeticOperation(leftExpression, rightExpression, leftExpression.type)

data class Subtraction(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ArithmeticOperation(leftExpression, rightExpression, leftExpression.type)

data class Division(
    override val leftExpression: Expression, override val rightExpression: Expression
) : ArithmeticOperation(leftExpression, rightExpression, leftExpression.type)
