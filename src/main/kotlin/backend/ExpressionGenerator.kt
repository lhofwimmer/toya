package backend

import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.Type

class ExpressionGenerator(private val methodVisitor: MethodVisitor) {
    fun generate(expression: Expression, scope: Scope) {
        when (expression) {
            is VarReference -> generate(expression, scope)
            is FunctionCall -> generate(expression, scope)
            is FunctionParameter -> generate(expression, scope)
            is Value -> generate(expression, scope)
            is ArithmeticOperation -> generate(expression, scope)
        }
    }

    private fun generate(expression: ArithmeticOperation, scope: Scope) {
        generate(expression.leftExpression, scope)
        generate(expression.rightExpression, scope)
        when(expression) {
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

        if (type == BuiltInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index)
        } else if (type == BuiltInType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.AALOAD, index)
        }
    }

    private fun generate(parameter: FunctionParameter, scope: Scope) {
        val type = parameter.type
        val index = scope.getLocalVariableIndex(parameter.name)
        if (type == BuiltInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index)
        } else if (type == BuiltInType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.AALOAD, index)
        }
    }

    private fun generate(value: Value, scope: Scope) {
        val type = value.type
        val stringValue = value.value

        if (type == BuiltInType.INT) {
            val intValue = stringValue.toInt()
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue)
        } else if (type == BuiltInType.STRING) {
            methodVisitor.visitLdcInsn(stringValue)
        }
    }

    private fun generate(functionCall: FunctionCall, scope: Scope) {
        val parameters = functionCall.parameters
        parameters.forEach { generate(it, scope) }
        val owner = functionCall.owner ?: ClassType(scope.getClassName())

        val methodDescriptor = getFunctionDescriptor(functionCall, scope)
        val ownerDescriptor = owner.getInternalName()
        val functionName = functionCall.signature.name
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false)
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
            val owner = functionCall.owner
            val className = owner?.typeName ?: scope.getClassName()
            val kClass = Class.forName(className).kotlin
            val method = kClass.java.getMethod(functionName)
            Type.getMethodDescriptor(method)
        } catch (ex: ReflectiveOperationException) {
            null
        }
    }
}