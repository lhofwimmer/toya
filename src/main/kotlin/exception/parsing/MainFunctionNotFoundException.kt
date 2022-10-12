package exception.parsing

object MainFunctionNotFoundException : ParsingException("Main function `function main() {}` not found in sources.")