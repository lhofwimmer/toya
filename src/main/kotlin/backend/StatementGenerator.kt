package backend

import ast.expression.Expression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.PrintStatement
import ast.statement.Statement
import ast.statement.VariableDeclarationStatement
import ast.type.BasicType
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

class StatementGenerator(mv: MethodVisitor, private val scope: Scope) {
    private val expressionGenerator: ExpressionGenerator
    private val methodVisitor: MethodVisitor = mv

    init {
        expressionGenerator = ExpressionGenerator(methodVisitor)
    }

    fun generate(statement: Statement, scope: Scope) {
        when(statement) {
            is PrintStatement -> generate(statement, scope)
            is VariableDeclarationStatement -> generate(statement, scope)
            is Expression -> expressionGenerator.generate(statement, scope)
        }
    }

    private fun generate(printStatement: PrintStatement, scope: Scope) {
        val expressionGenerator = ExpressionGenerator(methodVisitor)
        val expression = printStatement.expression
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        expressionGenerator.generate(expression, scope)
        val type = expression.type
        val descriptor = "(${type.getDescriptor()})V"
        val fieldDescriptor = "Ljava/io/PrintStream;"
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false)
    }

    private fun generate(variableDeclarationStatement: VariableDeclarationStatement, scope: Scope) {
        val expression = variableDeclarationStatement.expression
        val name = variableDeclarationStatement.name
        val index = scope.getLocalVariableIndex(name)
        val type = expression.type

        expressionGenerator.generate(expression, scope)

        methodVisitor.visitVarInsn(if(type== BasicType.INT) Opcodes.ISTORE else Opcodes.ASTORE, index)

        scope.addLocalVariable(LocalVariable(name, expression.type))
    }

}