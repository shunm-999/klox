import java.io.PrintWriter
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val path = if (args.isEmpty()) {
        Paths.get("").toAbsolutePath().toString()
    } else {
        args[0]
    }
    GenerateAst.generate(arrayOf(path))
}

object GenerateAst {
    fun generate(args: Array<String>) {
        if (args.size != 1) {
            println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]

        defineAst(
            outputDir = outputDir,
            "Expr",
            listOf(
                "Binary   : val left: Expr, val operator: Token, val right: Expr",
                "Grouping : val expression: Expr",
                "Literal  : val value: Object",
                "Unary    : val operator: Token, val right: Expr"
            )
        )
    }

    private fun defineAst(
        outputDir: String,
        baseName: String,
        types: List<String>
    ) {
        val path = "$outputDir/$baseName.kt"

        PrintWriter(path, "UTF-8").use { writer ->
            writer.println()
            writer.println("sealed interface $baseName {")

            for (type in types) {
                val className = type.split(":", limit = 2)[0].trim()
                val fields = type.split(":", limit = 2)[1].trim()

                defineType(
                    writer = writer,
                    baseName = baseName,
                    className = className,
                    fields = fields,
                )
                writer.println("")
            }

            writer.println("}")
        }
    }

    private fun defineType(
        writer: PrintWriter,
        baseName: String,
        className: String,
        fields: String
    ) {
        writer.println("data class $className($fields) : $baseName")
    }
}