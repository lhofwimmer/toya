package parsing.visitor

import ast.expression.FunctionArgument
import ast.scope.FunctionSignature
import gen.toyaBaseVisitor
import gen.toyaParser
import util.TypeResolver

class FunctionSignatureVisitor : toyaBaseVisitor<FunctionSignature>() {
    override fun visitFunctionSignature(ctx: toyaParser.FunctionSignatureContext): FunctionSignature {
        val functionName = ctx.name().text
        val argsCtx = ctx.functionArgument()
        val parameters = argsCtx.map {argCtx ->
            val name = argCtx.name().text
            val type = TypeResolver.getFromTypeName(argCtx.type())
            FunctionArgument(name, type)
        }
        val returnType = TypeResolver.getFromTypeName(ctx.type())
        return FunctionSignature(functionName, parameters, returnType)
    }
}
