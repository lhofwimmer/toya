package parsing.visitor

import ast.Compilation
import ast.scope.LocalVariable
import ast.scope.Scope
import gen.toyaBaseVisitor
import gen.toyaParser

class CompilationVisitor : toyaBaseVisitor<Compilation>() {
    override fun visitCompilation(ctx: toyaParser.CompilationContext): Compilation {
        val globalScope = Scope()

        val localVariables = ctx.variableDeclaration().map { varDeclarationCtx ->
            val expression = varDeclarationCtx.expression().accept(ExpressionVisitor(Scope()))
            val lv = LocalVariable(varDeclarationCtx.name().text, expression.type)
            globalScope.addLocalVariable(lv)
            lv
        }

        val functions = ctx.function().map { functionCtx ->
            val f = functionCtx.accept(FunctionVisitor(globalScope))
            globalScope.addSignature(functionCtx.functionSignature().accept(FunctionSignatureVisitor()))
            f
        }

        return Compilation(
            functions,
            localVariables,
            globalScope
        )
    }
}