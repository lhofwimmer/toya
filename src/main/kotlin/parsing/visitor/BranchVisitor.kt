package parsing.visitor

import ast.expression.Branch
import ast.expression.BranchExpression
import ast.expression.BranchStatement
import ast.scope.Scope
import gen.toyaBaseVisitor
import gen.toyaParser

class BranchVisitor(val scope: Scope) : toyaBaseVisitor<Branch>() {
    override fun visitBlock(ctx: toyaParser.BlockContext): Branch {
        return BranchStatement(
            ctx.statement().map {
                it.accept(StatementVisitor(scope)) // TODO: give each branch block its own scope
            }
        )
    }

    override fun visitExp(ctx: toyaParser.ExpContext): Branch {
        return BranchExpression(
            ctx.expression().accept(ExpressionVisitor(scope))
        )
    }
}