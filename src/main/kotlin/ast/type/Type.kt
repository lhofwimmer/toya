package ast.type

interface Type {
    val typeName: String
    fun getDescriptor() : String
}