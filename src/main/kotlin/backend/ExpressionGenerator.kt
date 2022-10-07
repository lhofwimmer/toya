@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package backend

import ast.expression.*
import ast.scope.Scope
import ast.type.BasicType
import exception.compilation.CalledFunctionDoesNotExistException
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import util.DescriptorUtil
import util.isStandardFunction

class ExpressionGenerator(private val methodVisitor: MethodVisitor) {
    fun generate(expression: Expression, scope: Scope) {
        when (expression) {
            is VarReference -> generate(expression, scope)
            is FunctionCall -> generate(expression, scope)
            is FunctionArgument -> generate(expression, scope)
            is Value -> generate(expression, scope)
            is ArithmeticOperation -> generate(expression, scope)
        }
    }

    private fun generate(expression: ArithmeticOperation, scope: Scope) {
        generate(expression.leftExpression, scope)
        generate(expression.rightExpression, scope)
        when (expression) {
            is Addition -> methodVisitor.visitInsn(Opcodes.IADD)
            is Division -> methodVisitor.visitInsn(Opcodes.IDIV)
            is Multiplication -> methodVisitor.visitInsn(Opcodes.IMUL)
            is Subtraction -> methodVisitor.visitInsn(Opcodes.IDIV)
        }
    }

    private fun generate(varReference: VarReference, scope: Scope) {
        val varName = varReference.varName
        val index = scope.getLocalVariableIndex(varName)
        val localVariable = scope.getLocalVariable(varName)
        val type = localVariable.type

        if (type == BasicType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index)
        } else if (type == BasicType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.AALOAD, index)
        }
    }

    private fun generate(parameter: FunctionArgument, scope: Scope) {
        val type = parameter.type
        val index = scope.getLocalVariableIndex(parameter.name)
        if (type == BasicType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index)
        } else if (type == BasicType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.AALOAD, index)
        }
    }

    private fun generate(value: Value, scope: Scope) {
        val type = value.type
        val stringValue = value.value

        if (type == BasicType.INT) {
            val intValue = stringValue.toInt()
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue)
        } else if (type == BasicType.STRING) {
            methodVisitor.visitLdcInsn(stringValue)
        }
    }

    private fun generate(functionCall: FunctionCall, scope: Scope) {
        val parameters = functionCall.parameters
        parameters.forEach { generate(it, scope) }
        val methodDescriptor = getFunctionDescriptor(functionCall, scope)
        val functionName = functionCall.signature.name

        if (isStandardFunction(functionName, parameters)) {
        } else {
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                DescriptorUtil.getOwnerDescriptor(),
                functionName,
                methodDescriptor,
                false
            )
        }
    }

    private fun getFunctionDescriptor(functionCall: FunctionCall, scope: Scope): String {
        return (getDescriptorForFunctionInScope(functionCall, scope)
            ?: getDescriptorForFunctionOnClasspath(functionCall, scope)) ?: throw CalledFunctionDoesNotExistException(
            functionCall, scope
        )
    }

    private fun getDescriptorForFunctionInScope(functionCall: FunctionCall, scope: Scope): String? {
        if (!scope.containsSignature(functionCall.signature)) return null
        return DescriptorUtil.getMethodDescriptor(functionCall.signature)
    }

    private fun getDescriptorForFunctionOnClasspath(functionCall: FunctionCall, scope: Scope): String? {
        return try {
            val functionName = functionCall.signature.name
            val parameters = functionCall.parameters
            val owner = DescriptorUtil.getOwnerDescriptor()
            val kClass = Class.forName("Main").kotlin
            val method = kClass.java.getMethod(functionName)
            Type.getMethodDescriptor(method)
        } catch (ex: ReflectiveOperationException) {
            null
        }
    }
}