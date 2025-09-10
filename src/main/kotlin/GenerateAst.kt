import java.io.PrintWriter
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val path =
        if (args.isEmpty()) {
            Paths.get("").toAbsolutePath().toString()
        } else {
            args[0]
        }
    GenerateAst.generate(arrayOf(path))
}

object GenerateAst {
    internal data class IndentableWriter(
        val level: Int = 0,
        val indent: String = " ",
        val writer: PrintWriter,
    ) {
        fun write(x: String) {
            writer.print(indent.repeat(level))
            writer.println(x)
        }
    }

    fun generate(args: Array<String>) {
        if (args.size != 1) {
            println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]
        val baseName = "Expr"
        val path = "$outputDir/$baseName.kt"

        define(path = path) {
            defineAst(
                "Expr",
                listOf(
                    "Binary   : val left: Expr, val operator: Token, val right: Expr",
                    "Grouping : val expression: Expr",
                    "Literal  : val value: Any?",
                    "Unary    : val operator: Token, val right: Expr",
                    "Variable : val name: Token",
                ),
            )
            defineAst(
                "Stmt",
                listOf(
                    "Expression : val expression: Expr",
                    "Print : val expression: Expr",
                    "Var: val token: Token, val initializer: Expr",
                ),
            )
        }
    }

    private fun define(
        path: String,
        action: IndentableWriter.() -> Unit,
    ) {
        PrintWriter(path, "UTF-8").use { writer ->
            val scope =
                IndentableWriter(
                    level = 0,
                    writer = writer,
                )
            scope.action()
        }
    }

    context(writer: IndentableWriter)
    private fun withIndent(action: IndentableWriter.() -> Unit) {
        val newWriter =
            IndentableWriter(
                level = writer.level + 4,
                writer = writer.writer,
                indent = writer.indent,
            )
        newWriter.action()
    }

    context(writer: IndentableWriter)
    private fun defineAst(
        baseName: String,
        types: List<String>,
    ) {
        writer.write("sealed interface $baseName {")

        withIndent {
            defineVisitor(
                baseName = baseName,
                types = types,
            )
        }
        writer.write("")

        withIndent {
            write("fun <R> accept(visitor: Visitor<R>): R")
        }
        writer.write("")

        for (type in types) {
            val className = type.split(":", limit = 2)[0].trim()
            val fields = type.split(":", limit = 2)[1].trim()

            withIndent {
                defineType(
                    baseName = baseName,
                    className = className,
                    fields = fields,
                )
            }
            writer.write("")
        }

        writer.write("}")
    }

    context(writer: IndentableWriter)
    private fun defineType(
        baseName: String,
        className: String,
        fields: String,
    ) {
        writer.write("data class $className($fields) : $baseName {")
        withIndent {
            write("override fun <R> accept(visitor: Visitor<R>): R {")
            withIndent {
                write("return visitor.visit${className}$baseName(this)")
            }
            write("}")
        }
        writer.write("}")
    }

    context(writer: IndentableWriter)
    private fun defineVisitor(
        baseName: String,
        types: List<String>,
    ) {
        writer.write("interface Visitor<R> {")
        for (type in types) {
            val typeName = type.split(":", limit = 2)[0].trim()
            withIndent {
                write("fun visit${typeName}$baseName(${baseName.lowercase()}: $typeName): R")
            }
        }
        writer.write("}")
    }
}
