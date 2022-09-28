package util

import java.io.File
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

fun List<File>.mergeFiles(): String {
    return this.fold("") { acc, file ->
        acc + file.readText()
    }
}

fun <R> List<File>.use(block: (Path) -> R) : R {
    val path = kotlin.io.path.createTempFile()
    path.writeText(this.mergeFiles())
    val value = block(path)
    path.deleteIfExists()
    return value
}