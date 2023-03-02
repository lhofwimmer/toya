package ast.type

import kotlin.reflect.KClass

enum class BasicType(
    override val typeName: String,
    private val typeClass: KClass<*>?,
    private val descriptor: String
) : Type {
    BOOLEAN("boolean", Boolean::class, "Z"),
    INT("int", Int::class, "I"),
    DOUBLE("double", Double::class, "D"),
    STRING("string", String::class, "Ljava/lang/String;"),

    BOOLEAN_ARR("boolean[]", BooleanArray::class, "[B"),
    INT_ARR("int[]", IntArray::class, "[I"),
    DOUBLE_ARR("double[]", DoubleArray::class, "[D"),
    STRING_ARR("string[]", Array<String>::class, "[Ljava/lang/String;"),

    NONE("", null, ""),
    VOID("void", Void::class , "V");

    override fun getDescriptor(): String = descriptor
}