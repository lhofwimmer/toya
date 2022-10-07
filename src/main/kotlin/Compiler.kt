import ast.Compilation
import backend.ByteCodeGenerator
import parsing.Parser
import java.io.File
import java.io.FileOutputStream

object Compiler {
    fun compile(args: Array<String>) {
        when(val argumentError = areValidArgs(args)) {
            ArgumentError.NONE -> {}
            ArgumentError.BAD_FILE_EXTENSION -> {
                println(argumentError.description)
                println("The following files have the wrong extension:")
                args.filter { !it.endsWith(".toya") }.forEach { _ ->
                    println()
                }
                return
            }
            ArgumentError.NO_FILE -> {
                println(argumentError.description)
                return
            }
        }

        val files = args.map { File(it) }
        val ast = Parser().getCompilation(files)
        createByteCode(ast, files.firstOrNull()?.nameWithoutExtension ?: "main")
    }

    private fun createByteCode(ast: Compilation, className: String) {
        val byteCode = ByteCodeGenerator().generate(ast)
        val fileName = "$className.class"
        FileOutputStream(fileName).use {
            it.write(byteCode)
        }
    }

    private fun areValidArgs(args: Array<String>) : ArgumentError {
        if(args.isEmpty()) return ArgumentError.NO_FILE
        if(args.any { !it.endsWith(".toya") }) return ArgumentError.BAD_FILE_EXTENSION
        return ArgumentError.NONE
    }
}


enum class ArgumentError(
    val description: String
) {
    NONE(""),
    NO_FILE("Error: No files provided to compile!"),
    BAD_FILE_EXTENSION("Error: File needs to be of type .toya!")
}
