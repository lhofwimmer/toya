package exception.compilation

import ast.expression.FunctionCall

class CalledFunctionDoesNotExistException(private val functionCall: FunctionCall) :
    CompilationException("Called function $functionCall does not exist.")