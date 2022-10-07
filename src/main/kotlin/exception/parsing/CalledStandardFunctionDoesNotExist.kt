package exception.parsing

import ast.expression.Expression


class StandardFunctionDoesNotExistException(functionName: String, arguments: List<Expression>) :
    RuntimeException(
        "Standard function with name $functionName and arguments: <${arguments.joinToString(",") { it.type.typeName }}> "
    )