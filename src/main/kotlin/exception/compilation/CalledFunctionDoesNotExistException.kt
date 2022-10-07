package exception.compilation

import ast.expression.FunctionCall
import ast.scope.Scope

class CalledFunctionDoesNotExistException(private val functionCall: FunctionCall) : CompilationException() {
    constructor(functionCall: FunctionCall, e: ReflectiveOperationException) : this(functionCall)
    constructor(functionCall: FunctionCall, scope: Scope) : this(functionCall)
    override fun getLocalizedMessage(): String {
        return "Function call $functionCall does not exist"
    }
}