package backend

import ast.expression.Expression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.ForStatement
import ast.statement.Statement
import ast.statement.VariableAssertionStatement
import ast.statement.VariableDeclarationStatement
import ast.type.BasicType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import util.StandardFunction
import util.standardFunctions

class StatementGenerator(private val mv: MethodVisitor) {
    private val expressionGenerator = ExpressionGenerator(mv)
    private val standardFunctionGenerator= StandardFunctionGenerator(mv)

    fun generate(statement: Statement, scope: Scope) {
        when (statement) {
            is StandardFunction -> standardFunctions
            is VariableDeclarationStatement -> generate(statement, scope)
            is Expression -> expressionGenerator.generate(statement, scope)
        }
    }

    private fun generate(variableDeclarationStatement: VariableDeclarationStatement, scope: Scope) {
        val expression = variableDeclarationStatement.expression
        val name = variableDeclarationStatement.name
        val index = scope.getLocalVariableIndex(name)

        expressionGenerator.generate(expression, scope)

        val storeType = when(expression.type) {
            BasicType.INT, BasicType.SHORT, BasicType.BOOLEAN, BasicType.BYTE, BasicType.CHAR -> Opcodes.ISTORE
            BasicType.FLOAT -> Opcodes.FSTORE
            BasicType.DOUBLE -> Opcodes.DSTORE
            BasicType.LONG -> Opcodes.LSTORE
            BasicType.STRING -> Opcodes.ASTORE
            else -> TODO()
        }

        // TODO handle array store

        mv.visitVarInsn(storeType, index)

        scope.addLocalVariable(LocalVariable(name, expression.type))
    }

    private fun generate(variableAssertionStatement: VariableAssertionStatement, scope: Scope) {

    }

    private fun generate(forStatement: ForStatement) {

    }
}