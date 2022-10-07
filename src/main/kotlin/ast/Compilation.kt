package ast

import ast.function.Function
import ast.scope.LocalVariable
import ast.scope.Scope

data class Compilation(
    val functions: List<Function>,
    val localVariables: List<LocalVariable>,
    val globalScope: Scope
)