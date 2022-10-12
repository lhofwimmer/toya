package exception.parsing

data class UnrecognizedComparatorException(
    val comparator: String
) : Exception("The token '$comparator' is not a known comparator.")