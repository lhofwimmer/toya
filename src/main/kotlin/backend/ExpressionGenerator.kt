@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package backend

import ast.expression.*
import ast.scope.Scope
import ast.type.BasicType
import exception.compilation.CalledFunctionDoesNotExistException
import exception.compilation.UnsupportedOperationOnTypeException
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import util.DescriptorUtil
import util.PrintFunction
import util.checkTypeMatch
import util.handleTypeGroups

class ExpressionGenerator(private val methodVisitor: MethodVisitor) {
    private val standardFunctionGenerator = StandardFunctionGenerator(methodVisitor)
    fun generate(expression: Expression, scope: Scope) {
        when (expression) {
            is VarReference -> generate(expression, scope)
            is FunctionCall -> generate(expression, scope)
            is FunctionArgument -> generate(expression, scope)
            is Value -> generate(expression)
            is ArithmeticOperation -> generate(expression, scope)
            is PrintFunction -> standardFunctionGenerator.generate(expression, scope)
            is ComparativeExpression -> generate(expression, scope)
            is BooleanExpression -> generate(expression, scope)
            is NullExpression -> generateNull()
            is IfExpression -> generate(expression, scope)
        }
    }

    private fun generateNull() {
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)
    }

    private fun generate(booleanExpression: BooleanExpression, scope: Scope) {
        val left = booleanExpression.leftExpression
        val right = booleanExpression.rightExpression
        left.type.checkTypeMatch(right.type)
        if (left.type != BasicType.BOOLEAN) throw UnsupportedOperationOnTypeException(
            left.type,
            booleanExpression::class
        )

        val falseLabel = Label()
        val endLabel = Label()
        val trueLabel = Label()

        generate(left, scope)
        if (booleanExpression is AndExpression) {
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, falseLabel)
        } else {
            methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel)
        }

        generate(right, scope)
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, falseLabel)

        methodVisitor.visitLabel(trueLabel)
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel)
        methodVisitor.visitLabel(falseLabel)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitLabel(endLabel)
    }

    private fun generate(comparativeExpression: ComparativeExpression, scope: Scope) {
        val left = comparativeExpression.leftExpression
        val right = comparativeExpression.rightExpression
        left.type.checkTypeMatch(right.type)

        generate(left, scope)
        generate(right, scope)

        val trueLabel = Label()
        val endLabel = Label()

        val cmpInstruction = left.type.handleTypeGroups(
            i = {
                when (comparativeExpression) {
                    is EqualExpression -> Opcodes.IF_ICMPEQ
                    is GreaterEqualExpression -> Opcodes.IF_ICMPGT
                    is GreaterThanExpression -> Opcodes.IF_ICMPGT
                    is LessEqualExpression -> Opcodes.IF_ICMPLE
                    is LessThanExpression -> Opcodes.IF_ICMPLT
                    is NotEqualExpression -> Opcodes.IF_ICMPNE
                }
            },
            d = {
                methodVisitor.visitInsn(Opcodes.DCMPL)
                when (comparativeExpression) {
                    is EqualExpression -> Opcodes.IFEQ
                    is GreaterEqualExpression -> Opcodes.IFGE
                    is GreaterThanExpression -> Opcodes.IFGT
                    is LessEqualExpression -> Opcodes.IFLE
                    is LessThanExpression -> Opcodes.IFLT
                    is NotEqualExpression -> Opcodes.IFNE
                }
            },
            a = {
                when (comparativeExpression) {
                    is EqualExpression -> Opcodes.IF_ACMPEQ
                    is NotEqualExpression -> Opcodes.IF_ACMPNE
                    else -> throw UnsupportedOperationOnTypeException(left.type, comparativeExpression::class)
                }
            },
            z = { throw UnsupportedOperationOnTypeException(left.type, comparativeExpression::class) }
        )

        methodVisitor.visitJumpInsn(cmpInstruction, trueLabel)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel)
        methodVisitor.visitLabel(trueLabel)
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitLabel(endLabel)
    }

    private fun generate(expression: ArithmeticOperation, scope: Scope) {
        val left = expression.leftExpression
        val right = expression.rightExpression
        left.type.checkTypeMatch(right.type)

        generate(left, scope)
        generate(right, scope)
        val instruction = left.type.handleTypeGroups(
            i = {
                when (expression) {
                    is Addition -> Opcodes.IADD
                    is Division -> Opcodes.IDIV
                    is Multiplication -> Opcodes.IMUL
                    is Subtraction -> Opcodes.IDIV
                }
            },
            d = {
                when (expression) {
                    is Addition -> Opcodes.DADD
                    is Division -> Opcodes.DDIV
                    is Multiplication -> Opcodes.DMUL
                    is Subtraction -> Opcodes.DDIV
                }
            },
            a = {
                throw UnsupportedOperationOnTypeException(left.type, expression::class)
            },
            z = {
                throw UnsupportedOperationOnTypeException(left.type, expression::class)
            }
        )

        methodVisitor.visitInsn(instruction)
    }

    private fun generate(varReference: VarReference, scope: Scope) {
        val varName = varReference.varName
        val index = scope.getLocalVariableIndex(varName)
        val localVariable = scope.getLocalVariable(varName)
        val type = localVariable.type

        val instruction = type.handleTypeGroups(
            i = { Opcodes.ILOAD },
            d = { Opcodes.DLOAD },
            a = { Opcodes.ALOAD },
            z = { Opcodes.ILOAD }
        )
        methodVisitor.visitVarInsn(instruction, index)
    }

    private fun generate(parameter: FunctionArgument, scope: Scope) {
        val type = parameter.type
        val index = scope.getLocalVariableIndex(parameter.name)

        val instruction = type.handleTypeGroups(
            i = { Opcodes.ILOAD },
            d = { Opcodes.DLOAD },
            a = { Opcodes.ALOAD },
            z = { Opcodes.ILOAD }
        )
        methodVisitor.visitVarInsn(instruction, index)
    }

    private fun generate(value: Value) {
        val type = value.type
        val stringValue = value.value

        type.handleTypeGroups(
            i = {
                val intValue = stringValue.toInt()
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue)
            },
            d = {
                val doubleValue = stringValue.toDouble()
                methodVisitor.visitLdcInsn(doubleValue)
            },
            a = { methodVisitor.visitLdcInsn(stringValue) },
            z = {
                val opcode = if (stringValue == "true") Opcodes.ICONST_1 else Opcodes.ICONST_0
                methodVisitor.visitInsn(opcode)
            }
        )
    }

    private fun generate(functionCall: FunctionCall, scope: Scope) {
        val parameters = functionCall.parameters
        parameters.forEach { generate(it, scope) }
        val methodDescriptor = getFunctionDescriptor(functionCall, scope)
        val functionName = functionCall.signature.name

        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            DescriptorUtil.getOwnerDescriptor(),
            functionName,
            methodDescriptor,
            false
        )
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