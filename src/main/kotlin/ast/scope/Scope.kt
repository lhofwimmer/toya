package ast.scope

import exception.parsing.LocalVariableNotFoundException
import exception.parsing.MethodSignatureNotFoundException

class Scope {
    private val localVariables: MutableList<LocalVariable> = mutableListOf()
    private var functionSignatures: MutableList<FunctionSignature>

    constructor(scope: Scope) {
        functionSignatures = scope.functionSignatures.toMutableList()
    }

    constructor() {
        functionSignatures = mutableListOf()
    }

    fun addSignature(signature: FunctionSignature) {
        functionSignatures.add(signature)
    }

    fun getSignature(methodName: String) = functionSignatures.firstOrNull { it.name == methodName }
        ?: throw MethodSignatureNotFoundException(this, methodName)

    fun containsSignature(functionSignature: FunctionSignature) = functionSignatures.contains(functionSignature)

    fun addLocalVariable(localVariable: LocalVariable) = localVariables.add(localVariable)

    fun getLocalVariable(varName: String) = localVariables.firstOrNull { it.name == varName }
        ?: throw LocalVariableNotFoundException(this, varName)

    fun getLocalVariableIndex(varName: String) = localVariables.indexOf(getLocalVariable(varName))
    fun getLocalVariableIndex(localVariable: LocalVariable) = localVariables.indexOf(localVariable)
}