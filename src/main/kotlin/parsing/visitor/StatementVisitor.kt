package parsing.visitor

import ast.expression.EmptyExpression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.ReturnStatement
import ast.statement.Statement
import ast.statement.VariableDeclarationStatement
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
}