package parsing.visitor

import exception.parsing.NoVisitorReturnedValueException
import gen.toyaBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext

class CompositeVisitor<T>(private vararg val visitors: toyaBaseVisitor<out T>) {
    fun accept(context: ParserRuleContext): T {
        return visitors.map(context::accept).first {it != null} ?: throw NoVisitorReturnedValueException
    }
}