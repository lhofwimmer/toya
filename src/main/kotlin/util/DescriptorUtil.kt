package util

import ast.expression.FunctionArgument
import ast.function.Function
import ast.scope.FunctionSignature
import ast.type.Type

object DescriptorUtil {
    fun getMethodDescriptor(function: Function): String = getMethodDescriptor(function.arguments, function.returnType)

    fun getMethodDescriptor(signature: FunctionSignature): String =
        getMethodDescriptor(signature.arguments, signature.returnType)

    private fun getMethodDescriptor(arguments: List<FunctionArgument>, returnType: Type): String {
        val argumentDescriptor =
            arguments.joinToString(separator = "", prefix = "(", postfix = ")") { it.type.getDescriptor() }
        val returnDescriptor = returnType.getDescriptor()
        return argumentDescriptor + returnDescriptor
    }
}