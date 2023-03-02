package ast.scope

import ast.type.BasicType
import exception.parsing.LocalVariableNotFoundException
import exception.parsing.MethodSignatureNotFoundException

class Scope {
    private val localVariables: MutableList<LocalVariable>
    private var functionSignatures: MutableList<FunctionSignature>

    constructor(scope: Scope) {
        localVariables = scope.localVariables.toMutableList()
        functionSignatures = scope.functionSignatures.toMutableList()
    }

    constructor() {
        localVariables = mutableListOf()
        functionSignatures = mutableListOf()
    }

    fun addSignature(signature: FunctionSignature) {
        functionSignatures.add(signature)
    }

    fun getSignature(methodName: String) = functionSignatures.firstOrNull { it.name == methodName }
        ?: throw MethodSignatureNotFoundException(this, methodName)

    fun containsSignature(functionSignature: FunctionSignature) = functionSignatures.contains(functionSignature)

    fun addLocalVariable(localVariable: LocalVariable) {
        if(!localVariables.any { it.name == localVariable.name }) {
            localVariables.add(localVariable)
        }
    }

    fun getLocalVariable(varName: String) = localVariables.firstOrNull { it.name == varName }
        ?: throw LocalVariableNotFoundException(this, varName)

    fun getLocalVariableIndex(varName: String) : Int {
        // hotfix for handling 2-wide index types (double, long, etc)
        return localVariables.subList(0,localVariables.indexOf(getLocalVariable(varName)))
            .fold(0) { acc, next ->
                acc + if(next.type == BasicType.DOUBLE) 2 else 1
            }
    }
    fun getLocalVariableIndex(localVariable: LocalVariable) = localVariables.indexOf(localVariable)
}