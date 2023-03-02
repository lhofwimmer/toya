package parsing.visitor

import ast.expression.ArrayDeclarationStatement
import ast.expression.EmptyExpression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.*
import ast.type.BasicType
import exception.parsing.VariableNameIsKeywordException
import gen.toyaBaseVisitor
import gen.toyaParser
import util.TypeResolver
import util.isReservedKeyword
import util.toArrayType

class StatementVisitor(val scope: Scope) : toyaBaseVisitor<Statement>() {
    override fun visitVariableDeclaration(ctx: toyaParser.VariableDeclarationContext): Statement {
        val varName = ctx.name().text
        if(varName.isReservedKeyword()) throw VariableNameIsKeywordException(varName)
        val expression = ctx.expression().accept(ExpressionVisitor(scope))
        scope.addLocalVariable(LocalVariable(varName, expression.type))
        return VariableDeclarationStatement(varName, expression)
    }

    override fun visitArrayDeclaration(ctx: toyaParser.ArrayDeclarationContext): Statement {
        val name = ctx.name().text
        if(name.isReservedKeyword()) throw VariableNameIsKeywordException(name)
        val type = TypeResolver.getFromTypeName(ctx.arrayType().text).toArrayType()
        val size = ctx.arrayDimension().expression().accept(ExpressionVisitor(scope))
        scope.addLocalVariable(LocalVariable(name, type))
        return ArrayDeclarationStatement(name, size, type)
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
        val localVariable = scope.getLocalVariable(name)
        val isArray = ctx.arrayExpression() != null
        val expressionVisitor = ExpressionVisitor(scope)
        val arrayExpression = if(isArray) ctx.arrayExpression()?.expression()?.accept(ExpressionVisitor(scope)) else null
        val expression = ctx.expression().accept(expressionVisitor)
        return VariableAssertionStatement(
            localVariable,
            isArray,
            arrayExpression,
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
        val statementVisitor = StatementVisitor(forScope)

        val forCondition = ctx.forHead().forCondition()?.expression()?.accept(expressionVisitor)
        val incrementExpression = ctx.forHead().incrementStatement()?.statement()?.accept(this)

        val forHead = ForHead(variableDeclaration, forCondition, incrementExpression)
        val compositeVisitor = CompositeVisitor(statementVisitor, expressionVisitor)
        val forStatements = ctx.statement().map(compositeVisitor::accept).toList()
        return ForStatement(forHead, forStatements, forScope)
    }
}