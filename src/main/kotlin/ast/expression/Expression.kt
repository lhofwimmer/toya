package ast.expression

import ast.statement.Statement
import ast.type.Type

open class Expression (
    open val type: Type
) : Statement