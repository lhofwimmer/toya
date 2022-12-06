package parsing.visitor

import ast.Compilation
import ast.expression.FunctionArgument
import ast.scope.FunctionSignature
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.type.BasicType
import exception.parsing.MainFunctionNotFoundException
import gen.toyaBaseVisitor
import gen.toyaParser

class CompilationVisitor : toyaBaseVisitor<Compilation>() {
    override fun visitCompilation(ctx: toyaParser.CompilationContext): Compilation {
        val globalScope = Scope()

        val globalVariables = ctx.variableDeclaration().map { varDeclarationCtx ->
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

        if (!containsMainFunction(globalScope)) throw MainFunctionNotFoundException

        return Compilation(
            functions,
            globalVariables,
            globalScope
        )
    }

    private fun containsMainFunction(scope: Scope): Boolean =
        scope.containsSignature(
            FunctionSignature(
                "main",
                listOf(FunctionArgument("args", BasicType.STRING_ARR)),
                BasicType.VOID
            )
        )
}