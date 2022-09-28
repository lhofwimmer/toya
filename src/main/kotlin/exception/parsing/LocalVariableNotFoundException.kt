package exception.parsing

import ast.scope.LocalVariable
import ast.scope.Scope

class LocalVariableNotFoundException(
    scope: Scope,
    localVariable: LocalVariable
) : RuntimeException("No local variable '${localVariable.name}' with type '${localVariable.type}' found in scope '$scope'")