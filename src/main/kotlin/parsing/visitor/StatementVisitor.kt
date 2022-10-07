package parsing.visitor

import ast.expression.EmptyExpression
import ast.expression.Expression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.*
import ast.type.BasicType
import gen.toyaBaseVisitor
import gen.toyaParser

class StatementVisitor(val scope: Scope) : toyaBaseVisitor<Statement>() {
    override fun visitVariableDeclaration(ctx: toyaParser.VariableDeclarationContext): Statement {
        val varName = ctx.name().text
        val expression = ctx.expression().accept(ExpressionVisitor(scope))
        scope.addLocalVariable(LocalVariable(varName, expression.type))
        return VariableDeclarationStatement(varName, expression)
    }

    override fun visitReturnVoid(ctx: toyaParser.ReturnVoidContext): Statement {
        return ReturnStatement(EmptyExpression(BasicType.VOID))
    }

    override fun visitReturnValue(ctx: toyaParser.ReturnValueContext): Statement {
        val expression = ctx.expression().accept(ExpressionVisitor(scope))
        return ReturnStatement(expression)
    }

    override fun visitVariableAssertion(ctx: toyaParser.VariableAssertionContext): Statement {
        val name = ctx.name().text
        val isArray = !ctx.arrayExpression().isNullOrEmpty()
        val expressionVisitor = ExpressionVisitor(scope)
        val arrayExpressionList =
            if (!isArray) ctx.arrayExpression().map { it.accept(ExpressionVisitor(scope)) } else null
        val expression = ctx.expression().accept(expressionVisitor)
        return VariableAssertionStatement(
            name,
            isArray,
            arrayExpressionList,
            expression
        )
    }

    override fun visitForStatement(ctx: toyaParser.ForStatementContext): Statement {
        val variableDeclaration: VariableDeclarationStatement? =
            ctx.forHead().variableDeclaration()?.accept(this) as VariableDeclarationStatement?

        val forScope = Scope(scope)
        if (variableDeclaration != null) forScope.addLocalVariable(
            LocalVariable(
                variableDeclaration.name,
                variableDeclaration.expression.type
            )
        )

        val expressionVisitor = ExpressionVisitor(forScope)
        val forCondition: Expression? = ctx.forHead().forCondition()?.accept(expressionVisitor)
        val incrementExpression: Expression? = ctx.forHead().incrementExpression()?.accept(expressionVisitor)

        val forHead = ForHead(variableDeclaration, forCondition, incrementExpression)
        val forStatements = ctx.statement().map {
            it.accept(this)
        }
        return ForStatement(forHead, forStatements, forScope)
    }
}