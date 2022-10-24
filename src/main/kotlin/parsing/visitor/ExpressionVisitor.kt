package parsing.visitor

import ast.expression.*
import ast.scope.Scope
import exception.parsing.StandardFunctionDoesNotExistException
import exception.parsing.UnrecognizedComparatorException
import gen.toyaBaseVisitor
import gen.toyaParser
import util.PrintFunction
import util.TypeResolver
import util.isStandardFunction

class ExpressionVisitor(val scope: Scope) : toyaBaseVisitor<Expression>() {
    override fun visitVarReference(ctx: toyaParser.VarReferenceContext): Expression {
        val varName = ctx.text
        val localVariable = scope.getLocalVariable(varName)
        return VarReference(varName, localVariable.type)
    }

    override fun visitValue(valueContext: toyaParser.ValueContext): Expression {
        val value = valueContext.text
        val type = TypeResolver.getFromValue(value)
        return Value(value, type)
    }

    override fun visitArrayAccess(ctx: toyaParser.ArrayAccessContext): Expression {
        val name = ctx.name().text
        val localVariable = scope.getLocalVariable(name)
        val position = ctx.arrayDimension().accept(this)
        return ArrayAccess(name, localVariable.type, position)
    }

    override fun visitFunctionCall(ctx: toyaParser.FunctionCallContext): Expression {
        val functionName = ctx.name().text
        val calledParameters = ctx.expression()
        val arguments = calledParameters.map { it.accept(ExpressionVisitor(scope)) }

        return if (!isStandardFunction(functionName, arguments)) {
            val signature = scope.getSignature(functionName)
            FunctionCall(signature, arguments)
        } else {
            when (functionName) {
                "print" -> PrintFunction(arguments.first())
                else -> throw StandardFunctionDoesNotExistException(functionName, arguments)
            }
        }
    }

    override fun visitIfExpression(ctx: toyaParser.IfExpressionContext): Expression {
        val ifCondition = ctx.ifCondition().accept(this)
        val branchVisitor = BranchVisitor(scope)
        val ifBranch = ctx.ifBranch().accept(branchVisitor)
        val elseBranch = ctx.elseBranch().accept(branchVisitor)
        return IfExpression(ifCondition, ifBranch, elseBranch)
    }

    override fun visitMatch(ctx: toyaParser.MatchContext): Expression {
        val matchHead = ctx.matchExpression().matchHead().expression().accept(this)
        val branchVisitor = BranchVisitor(scope)
        val branches = ctx.matchExpression().matchBranch().map {
            MatchBranch(
                Value(it.value().text, TypeResolver.getFromValue(it.value().text)),
                it.accept(branchVisitor)
            )
        }
        val default = ctx.matchExpression().matchDefault().accept(branchVisitor)
        return MatchExpression(
            matchHead,
            branches,
            default
        )
    }

    private fun visitBiOperator(
        leftExpression: toyaParser.ExpressionContext,
        rightExpression: toyaParser.ExpressionContext
    ): Pair<Expression, Expression> {
        return Pair(
            leftExpression.accept(this),
            rightExpression.accept(this)
        )
    }

    override fun visitAdd(ctx: toyaParser.AddContext): Expression {
        val (left, right) = visitBiOperator(ctx.expression(0), ctx.expression(1))
        return Addition(left, right)
    }

    override fun visitMultiply(ctx: toyaParser.MultiplyContext): Expression {
        val (left, right) = visitBiOperator(ctx.expression(0), ctx.expression(1))
        return Multiplication(left, right)
    }

    override fun visitSubtract(ctx: toyaParser.SubtractContext): Expression {
        val (left, right) = visitBiOperator(ctx.expression(0), ctx.expression(1))
        return Subtraction(left, right)
    }

    override fun visitDivide(ctx: toyaParser.DivideContext): Expression {
        val (left, right) = visitBiOperator(ctx.expression(0), ctx.expression(1))
        return Division(left, right)
    }

    override fun visitNotExpression(ctx: toyaParser.NotExpressionContext): Expression {
        return NotExpression(ctx.expression().accept(this))
    }

    override fun visitAndExpression(ctx: toyaParser.AndExpressionContext): Expression {
        return AndExpression(
            ctx.expression(0).accept(this),
            ctx.expression(1).accept(this)
        )
    }

    override fun visitOrExpression(ctx: toyaParser.OrExpressionContext): Expression {
        return OrExpression(
            ctx.expression(0).accept(this),
            ctx.expression(1).accept(this)
        )
    }

    override fun visitNullExpression(ctx: toyaParser.NullExpressionContext): Expression {
        return NullExpression
    }

    override fun visitBooleanExpression(ctx: toyaParser.BooleanExpressionContext): Expression {
        val (left, right) = visitBiOperator(ctx.expression(0), ctx.expression(1))
        val comparator = ctx.comparator()

        return when(comparator.start.type) {
            toyaParser.GT -> GreaterThanExpression(left, right)
            toyaParser.GE -> GreaterEqualExpression(left, right)
            toyaParser.LT -> LessThanExpression(left, right)
            toyaParser.LE -> LessEqualExpression(left, right)
            toyaParser.EQ -> EqualExpression(left, right)
            toyaParser.NE -> NotEqualExpression(left, right)
            toyaParser.AND -> AndExpression(left, right)
            toyaParser.OR -> OrExpression(left, right)
            else -> throw UnrecognizedComparatorException(comparator.text)
        }
    }
}