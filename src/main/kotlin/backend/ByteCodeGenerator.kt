package backend

import ast.Compilation
import com.sun.org.apache.xpath.internal.compiler.OpCodes
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Opcodes

class ByteCodeGenerator : OpCodes() {

    companion object {
        const val CLASS_VERSION = 52
        private val classWriter: ClassWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS)
    }

    fun generate(compilation: Compilation) : ByteArray {
        classWriter.visit(
            CLASS_VERSION,
            Opcodes.ACC_PUBLIC,
            "Main",
            null,
            "java/lang/Object",
            null
        )
        compilation.functions.forEach {
            FunctionGenerator(classWriter).generate(it)
        }
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }
}