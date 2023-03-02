package parsing.visitor

import ast.expression.Branch
import ast.expression.BranchExpression
import ast.expression.BranchStatement
import ast.scope.Scope
import ast.statement.Statement
import gen.toyaBaseVisitor
import gen.toyaParser

class BranchVisitor(val scope: Scope) : toyaBaseVisitor<Branch>() {
    override fun visitBlock(ctx: toyaParser.BlockContext): Branch {
        return BranchStatement(getStatements(ctx))

//        ctx.statement().map {
//            it.accept(StatementVisitor(scope)) // TODO: give each branch block its own scope
//        }
    }

    private fun getStatements(context: toyaParser.BlockContext): List<Statement> {
        val statementVisitor = StatementVisitor(scope)
        val expressionVisitor = ExpressionVisitor(scope)
        val compositeVisitor = CompositeVisitor(statementVisitor, expressionVisitor)
        return context.statement().map(compositeVisitor::accept).toList()
    }

    override fun visitExp(ctx: toyaParser.ExpContext): Branch {
        return BranchExpression(
            ctx.expression().accept(ExpressionVisitor(scope))
        )
    }
}