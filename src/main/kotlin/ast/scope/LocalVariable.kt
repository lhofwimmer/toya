package ast.scope

import ast.expression.Expression
import ast.type.Type

class LocalVariable(
    val name: String,
    override val type: Type
) : Expression(type)