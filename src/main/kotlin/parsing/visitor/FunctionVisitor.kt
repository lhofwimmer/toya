package parsing.visitor

import ast.expression.FunctionArgument
import ast.function.Function
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.Statement
import ast.type.Type
import gen.toyaBaseVisitor
import gen.toyaParser
import util.TypeResolver

class FunctionVisitor(scope: Scope) : toyaBaseVisitor<Function>() {
    private val scope: Scope
    init {
        this.scope = Scope(scope)
    }

    override fun visitFunction(ctx: toyaParser.FunctionContext): Function {
        val name = getName(ctx)
        val returnType = getReturnType(ctx)
        val arguments = getArguments(ctx)
        val statements = getStatements(ctx)
        return Function(name, arguments, statements, scope, returnType)
    }

    private fun getName(functionContext: toyaParser.FunctionContext): String {
        return functionContext.functionSignature().name().text
    }

    private fun getReturnType(functionContext: toyaParser.FunctionContext): Type {
        val typeCtx = functionContext.functionSignature().type()
        return TypeResolver.getFromTypeName(typeCtx)
    }

    private fun getArguments(functionContext: toyaParser.FunctionContext): List<FunctionArgument> {
        val argsCtx = functionContext.functionSignature().functionArgument()
        return argsCtx
            .map { FunctionArgument(it.name().text, TypeResolver.getFromTypeName(it.type())) }
            .onEach { scope.addLocalVariable(LocalVariable(it.name, it.type)) }
            .toList()
    }

    private fun getStatements(functionContext: toyaParser.FunctionContext): List<Statement> {
        val statementVisitor = StatementVisitor(scope)
        val expressionVisitor = ExpressionVisitor(scope)
        val compositeVisitor = CompositeVisitor(statementVisitor, expressionVisitor)
        return functionContext.statement().map(compositeVisitor::accept).toList()
    }
}