package parsing.visitor

import ast.expression.*
import ast.scope.Scope
import gen.toyaBaseVisitor
import gen.toyaParser
import gen.toyaParser.ArrayDimensionContext
import util.TypeResolver

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

    override fun visitArrayDeclaration(ctx: toyaParser.ArrayDeclarationContext): Expression {
        val type = TypeResolver.getFromValue(ctx.arrayType().text)
        val dimensions = getDimensions(ctx.arrayDimension())
        return ArrayDeclaration(dimensions, type)
    }

    override fun visitArrayAccess(ctx: toyaParser.ArrayAccessContext): Expression {
        val name = ctx.name().text
        val localVariable = scope.getLocalVariable(name)
        val dimensions = getDimensions(ctx.arrayDimension())
        return ArrayAccess(name, localVariable.type, dimensions)
    }

    private fun getDimensions(ctx: List<ArrayDimensionContext>): List<ArrayDimension> {
        return ctx.map {
            ArrayDimension(it.expression().accept(this))
        }
    }

    override fun visitFunctionCall(ctx: toyaParser.FunctionCallContext): Expression {
        val functionName = ctx.name().text
        val signature = scope.getSignature(functionName)
        val calledParameters = ctx.expression()
        val arguments = calledParameters.map { it.accept(ExpressionVisitor(scope)) }
        return FunctionCall(signature, arguments)
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

    private fun visitArithmeticOperation(
        leftExpression: toyaParser.ExpressionContext,
        rightExpression: toyaParser.ExpressionContext
    ): Pair<Expression, Expression> {
        return Pair(
            leftExpression.accept(this),
            rightExpression.accept(this)
        )
    }

    override fun visitAdd(ctx: toyaParser.AddContext): Expression {
        val (left, right) = visitArithmeticOperation(ctx.expression(0), ctx.expression(1))
        return Addition(left, right)
    }

    override fun visitMultiply(ctx: toyaParser.MultiplyContext): Expression {
        val (left, right) = visitArithmeticOperation(ctx.expression(0), ctx.expression(1))
        return Multiplication(left, right)
    }

    override fun visitSubtract(ctx: toyaParser.SubtractContext): Expression {
        val (left, right) = visitArithmeticOperation(ctx.expression(0), ctx.expression(1))
        return Subtraction(left, right)
    }

    override fun visitDivide(ctx: toyaParser.DivideContext): Expression {
        val (left, right) = visitArithmeticOperation(ctx.expression(0), ctx.expression(1))
        return Division(left, right)
    }
}