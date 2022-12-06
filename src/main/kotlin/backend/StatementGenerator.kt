package backend

import ast.expression.ArrayDeclarationStatement
import ast.expression.Expression
import ast.scope.LocalVariable
import ast.scope.Scope
import ast.statement.*
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import util.handleTypeArrays
import util.handleTypeGroups
import util.isArrayType

class StatementGenerator(private val mv: MethodVisitor) {
    private val expressionGenerator = ExpressionGenerator(mv)

    fun generate(statement: Statement, scope: Scope) {
        when (statement) {
            is VariableDeclarationStatement -> generate(statement, scope)
            is ArrayDeclarationStatement -> generate(statement, scope)
            is VariableAssertionStatement -> generate(statement, scope)
            is Expression -> expressionGenerator.generate(statement, scope)
            is ReturnStatement -> generate(statement, scope)
            is ForStatement -> generate(statement)
        }
    }

    private fun generate(variableDeclarationStatement: VariableDeclarationStatement, scope: Scope) {
        val expression = variableDeclarationStatement.expression
        val name = variableDeclarationStatement.name
        val index = scope.getLocalVariableIndex(name)

        expressionGenerator.generate(expression, scope)

        val storeType = expression.getStoreCode()

        mv.visitVarInsn(storeType, index)

        scope.addLocalVariable(LocalVariable(name, expression.type))
    }

    private fun generate(arrayDeclarationStatement: ArrayDeclarationStatement, scope: Scope) {
        val name = arrayDeclarationStatement.name
        val size = arrayDeclarationStatement.size
        val type = arrayDeclarationStatement.type
        val index = scope.getLocalVariableIndex(name)

        expressionGenerator.generate(size, scope)

        type.handleTypeArrays(
            ia = {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT)
            },
            da = {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE)
            },
            aa = {
                mv.visitTypeInsn(
                    Opcodes.ANEWARRAY,
                    Type.getInternalName(String::class.java) /*"java/lang/String"*/
                )
            },
            ba = {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN)
            },
        )
        mv.visitVarInsn(Opcodes.ASTORE, index)
        scope.addLocalVariable(LocalVariable(name, type))
    }

    private fun generate(returnStatement: ReturnStatement, scope: Scope) {
        expressionGenerator.generate(returnStatement.expression, scope)
        val opcode = if(returnStatement.expression.type.isArrayType()) {
            Opcodes.ARETURN
        } else {
            returnStatement.expression.type.handleTypeGroups(
                i = { Opcodes.IRETURN },
                d = { Opcodes.DRETURN },
                a = { Opcodes.ARETURN },
                z = { Opcodes.IRETURN }
            )
        }
        mv.visitInsn(opcode)
    }

    private fun generate(variableAssertionStatement: VariableAssertionStatement, scope: Scope) {
        val expression = variableAssertionStatement.expression
        val localVariable = variableAssertionStatement.localVariable
        val index = scope.getLocalVariableIndex(localVariable)

        if (variableAssertionStatement.isArray && variableAssertionStatement.arrayExpression != null) {
            mv.visitVarInsn(Opcodes.ALOAD, index)
            expressionGenerator.generate(variableAssertionStatement.arrayExpression, scope)
            expressionGenerator.generate(expression, scope)
            val opcode = localVariable.type.handleTypeArrays(
                ia = { Opcodes.IASTORE },
                da = { Opcodes.DASTORE },
                aa = { Opcodes.AASTORE },
                ba = { Opcodes.BASTORE }
            )
            mv.visitInsn(opcode)
        } else {
            expressionGenerator.generate(expression, scope)
            val storeType = expression.getStoreCode()
            mv.visitVarInsn(storeType, index)
        }

    }

    private fun Expression.getStoreCode() = this.type.handleTypeGroups(
        i = { Opcodes.ISTORE },
        d = { Opcodes.DSTORE },
        a = { Opcodes.ASTORE },
        z = { Opcodes.ISTORE }
    )

    private fun generate(forStatement: ForStatement) {
        val varStatement = forStatement.forHead.variableDeclaration
        val forCondition = forStatement.forHead.forCondition
        val incrementStatement = forStatement.forHead.incrementStatement

        val conditionLabel = Label()
        val endLabel = Label()

        if (varStatement != null) {
            generate(varStatement, forStatement.scope)
        }

        mv.visitLabel(conditionLabel)

        if (forCondition != null) {
            generate(forCondition, forStatement.scope)
            mv.visitJumpInsn(Opcodes.IFEQ, endLabel)
        }

        forStatement.statements.forEach { generate(it, forStatement.scope) }

        if (incrementStatement != null) {
            generate(incrementStatement, forStatement.scope)
        }

        mv.visitJumpInsn(Opcodes.GOTO, conditionLabel)

        mv.visitLabel(endLabel)
    }
}