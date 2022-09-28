package ast

import ast.function.Function
import ast.scope.LocalVariable

data class Compilation(
    val functions: List<Function>,
    val localVariables: List<LocalVariable>
)