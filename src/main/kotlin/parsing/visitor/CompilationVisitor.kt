package parsing.visitor

import ast.Compilation
import ast.scope.LocalVariable
import ast.scope.Scope
import gen.toyaBaseVisitor
import gen.toyaParser

class CompilationVisitor : toyaBaseVisitor<Compilation>() {
    override fun visitCompilation(ctx: toyaParser.CompilationContext): Compilation {
        val scope = Scope()
        val localVariables = ctx.variableDeclaration().map {
            val expression = it.expression().accept(ExpressionVisitor(Scope()))
            val lv = LocalVariable(it.name().text, expression.type)
            scope.addLocalVariable(lv)
            lv
        }

        val functions = ctx.function().map {
            it.accept(FunctionVisitor(scope))
        }

        return Compilation(
            functions,
            localVariables
        )
    }
}