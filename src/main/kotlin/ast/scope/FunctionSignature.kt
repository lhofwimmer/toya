package ast.scope

import ast.expression.FunctionArgument
import ast.type.Type

data class FunctionSignature(
    val name: String,
    val arguments: List<FunctionArgument>,
    val returnType: Type
)