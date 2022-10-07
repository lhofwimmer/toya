package backend

import ast.expression.EmptyExpression
import ast.function.Function
import ast.statement.ReturnStatement
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import util.DescriptorUtil

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
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(-1, -1)
        mv.visitEnd()
    }

    private fun appendReturnIfNotExists(function: Function, statementGenerator: StatementGenerator) {
        val lastStatement = function.statements.last()
        if (lastStatement !is ReturnStatement) {
            val emptyExpression = EmptyExpression(function.returnType)
        }
    }
}