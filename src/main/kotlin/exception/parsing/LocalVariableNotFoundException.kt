package exception.parsing

import ast.scope.LocalVariable
import ast.scope.Scope

class LocalVariableNotFoundException(
    scope: Scope,
    localVariable: String
) : ParsingException("No local variable '${localVariable}' found in scope '$scope'") {
    constructor(scope: Scope, localVariable: LocalVariable) : this(scope, localVariable.name)
}