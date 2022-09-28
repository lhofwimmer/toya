package ast.type

import kotlin.reflect.KClass

enum class BasicType(
    override val typeName: String,
    private val typeClass: KClass<*>?,
    private val descriptor: String
) : Type {
    BOOLEAN("boolean", Boolean::class, "Z"),
    INT("int", Int::class, "I"),
    CHAR("char", Char::class, "C"),
    BYTE("byte", Byte::class, "B"),
    SHORT("short", Short::class, "S"),
    LONG("long", Long::class, "J"),
    FLOAT("float", Float::class, "F"),
    DOUBLE("double", Double::class, "D"),
    STRING("string", String::class, "Ljava/lang/String;"),
    BOOLEAN_ARR("boolean[]", BooleanArray::class, "[B"),
    INT_ARR("int[]", IntArray::class, "[I"),
    CHAR_ARR("char[]", CharArray::class, "[C"),
    BYTE_ARR("byte[]", ByteArray::class, "[B"),
    SHORT_ARR("short[]", ShortArray::class, "[S"),
    LONG_ARR("long[]", LongArray::class, "[J"),
    FLOAT_ARR("float[]", FloatArray::class, "[F"),
    DOUBLE_ARR("double[]", DoubleArray::class, "[D"),
    STRING_ARR("string[]", Array<String>::class, "[Ljava/lang/String;"),
    NONE("", null, ""),
    VOID("void", Void::class , "V");

    override fun getTypeClass(): KClass<*>? = typeClass

    override fun getDescriptor(): String = descriptor
}