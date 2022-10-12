package backend

import ast.expression.Expression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.*
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import util.handleTypeGroups

class StatementGenerator(private val mv: MethodVisitor) {
    private val expressionGenerator = ExpressionGenerator(mv)

    fun generate(statement: Statement, scope: Scope) {
        when (statement) {
            is VariableDeclarationStatement -> generate(statement, scope)
            is Expression -> expressionGenerator.generate(statement, scope)
            is ReturnStatement -> generate(statement, scope)
        }
    }

    private fun generate(variableDeclarationStatement: VariableDeclarationStatement, scope: Scope) {
        val expression = variableDeclarationStatement.expression
        val name = variableDeclarationStatement.name
        val index = scope.getLocalVariableIndex(name)

        expressionGenerator.generate(expression, scope)

        val storeType = expression.type.handleTypeGroups(
            i = { Opcodes.ISTORE },
            d = { Opcodes.DSTORE },
            a = { Opcodes.ASTORE },
            z = { Opcodes.ISTORE }
        )

        // TODO handle array store

        mv.visitVarInsn(storeType, index)

        scope.addLocalVariable(LocalVariable(name, expression.type))
    }

    private fun generate(returnStatement: ReturnStatement, scope: Scope) {
        expressionGenerator.generate(returnStatement.expression, scope)
        returnStatement.expression.type.handleTypeGroups(
            i = { mv.visitInsn(Opcodes.IRETURN) },
            d = { mv.visitInsn(Opcodes.DRETURN) },
            a = { mv.visitInsn(Opcodes.ARETURN) },
            z = { mv.visitInsn(Opcodes.IRETURN) }
        )
    }

    private fun generate(variableAssertionStatement: VariableAssertionStatement, scope: Scope) {
    }

    private fun generate(forStatement: ForStatement) {

    }
}