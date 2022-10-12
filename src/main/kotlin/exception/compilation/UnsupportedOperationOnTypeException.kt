package exception.compilation

import ast.type.Type
import kotlin.reflect.KClass

class UnsupportedOperationOnTypeException(
    val type: Type,
    operation: KClass<*>
) : CompilationException("Operation `${operation.simpleName}` is not supported on type ${type.typeName}")