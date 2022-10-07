package exception.parsing

data class UnableToInferTypeException(
    val value: String
) : RuntimeException("Cannot infer type from value `$value`")