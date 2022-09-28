package ast.expression

import ast.scope.FunctionSignature

data class FunctionCall(
    val signature: FunctionSignature,
    val parameters: List<Expression>,
) : Expression(signature.returnType)