package ast.type

import kotlin.reflect.KClass

interface Type {
    val typeName: String
    fun getTypeClass() : KClass<*>?
    fun getDescriptor() : String
}