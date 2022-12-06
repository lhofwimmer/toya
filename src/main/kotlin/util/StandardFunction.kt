package util

import ast.expression.Expression
import ast.expression.FunctionArgument
import ast.scope.FunctionSignature
import ast.statement.Statement
import ast.type.BasicType

val standardFunctions = listOf( // for now only print and its overloads
    FunctionSignature("print", listOf(FunctionArgument("", BasicType.STRING)), BasicType.VOID),
    FunctionSignature("print", listOf(FunctionArgument("", BasicType.INT)), BasicType.VOID),
    FunctionSignature("print", listOf(FunctionArgument("", BasicType.DOUBLE)), BasicType.VOID),
    FunctionSignature("print", listOf(FunctionArgument("", BasicType.BOOLEAN)), BasicType.VOID),
)

sealed interface StandardFunction : Statement

data class PrintFunction(val message: Expression) : StandardFunction, Expression(BasicType.VOID)

fun isStandardFunction(name: String, arguments: List<Expression>): Boolean {
    return standardFunctions.any {// compares name and types of parameters
        it.name == name
                && arguments.size == it.arguments.size
                && it.arguments.zip(arguments).all { pair -> pair.first.type == pair.second.type }
    }
}

fun getSignatureByName(name: String): FunctionSignature {
    return standardFunctions.first { it.name == name }
}