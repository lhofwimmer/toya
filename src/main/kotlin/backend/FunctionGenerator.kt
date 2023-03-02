package backend

import ast.expression.Expression
import ast.function.Function
import ast.statement.ReturnStatement
import ast.type.BasicType
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import util.DescriptorUtil
import util.handleTypeGroups
import util.isArrayType

class FunctionGenerator(private val classWriter: ClassWriter) {
    fun generate(function: Function) {
        val scope = function.scope
        val name = function.name
        val description = DescriptorUtil.getMethodDescriptor(function)
        val instructions = function.statements
        val access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC

        val mv = classWriter.visitMethod(access, name, description, null, null)
        mv.visitCode()
        val statementGenerator = StatementGenerator(mv)
        instructions.forEach { statementGenerator.generate(it, scope) }
        appendReturnIfNotExists(function, mv)
        mv.visitMaxs(-1, -1)
        mv.visitEnd()
    }

    private fun appendReturnIfNotExists(function: Function, methodVisitor: MethodVisitor) {
        val lastStatement = function.statements.last()
        if (lastStatement !is ReturnStatement) {
            val opcode = if (lastStatement is Expression && function.returnType == lastStatement.type) {
                if (function.returnType == BasicType.VOID) {
                    Opcodes.RETURN
                } else {
                    if (lastStatement.type.isArrayType()) {
                        Opcodes.ARETURN
                    } else {
                        lastStatement.type.handleTypeGroups(
                            i = { Opcodes.IRETURN },
                            d = { Opcodes.DRETURN },
                            a = { Opcodes.ARETURN },
                            z = { Opcodes.IRETURN }
                        )
                    }
                }
            } else {
                Opcodes.RETURN
            }
            methodVisitor.visitInsn(opcode)
        }
    }
}