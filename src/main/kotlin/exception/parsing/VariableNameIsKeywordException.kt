package exception.parsing

data class VariableNameIsKeywordException(
    val variableName: String
) : ParsingException("The variable name `$variableName` cannot be used, because it is a reserved keyword")