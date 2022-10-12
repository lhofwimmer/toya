package exception.parsing

import ast.type.Type

class BinaryOperationTypeMismatchException(
    leftType: Type,
    rightType: Type
) : ParsingException("Binary operation with types left:${leftType.typeName}, right:${rightType.typeName} has type mismatch")