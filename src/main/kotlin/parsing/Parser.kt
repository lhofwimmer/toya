package parsing

import ast.Compilation
import gen.toyaLexer
import gen.toyaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import parsing.visitor.CompilationVisitor
import util.use
import java.io.File
import java.nio.file.Paths

class Parser {
    fun getCompilation(files: List<File>): Compilation {
        return files.use {path ->
            val charStream = CharStreams.fromPath(path)
            val lexer = toyaLexer(charStream)
            val tokenStream = CommonTokenStream(lexer)
            val parser = toyaParser(tokenStream)

            val errorListener = ToyaTreeWalkErrorListener()
            parser.addErrorListener(errorListener)

            val compilationVisitor = CompilationVisitor()
            parser.compilation().accept(compilationVisitor)
        }
    }
}
