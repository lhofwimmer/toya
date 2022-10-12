package exception.parsing

import ast.scope.Scope

class MethodSignatureNotFoundException(
    scope: Scope,
    methodName: String
) : ParsingException("No method '$methodName' found in scope '$scope'")