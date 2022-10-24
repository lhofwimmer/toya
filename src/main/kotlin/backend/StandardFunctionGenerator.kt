package backend

import ast.scope.Scope
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import util.PrintFunction
import util.StandardFunction

class StandardFunctionGenerator(private val mv: MethodVisitor, private val expressionGenerator: ExpressionGenerator) {

    fun generate(function: StandardFunction, scope: Scope) {
        when (function) {
            is PrintFunction -> generate(function, scope)
        }
    }

    private fun generate(printFunction: PrintFunction, scope: Scope) {
        val expression = printFunction.message
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        expressionGenerator.generate(expression, scope)
        val type = expression.type
        val descriptor = "(${type.getDescriptor()})V"
        val fieldDescriptor = "Ljava/io/PrintStream;"
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false)
    }
}