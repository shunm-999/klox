import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.system.exitProcess

object Lox {
    private var hadError = false
    private var hadRuntimeError = false

    private val logger = Logger()

    private val interpreter = Interpreter()

    fun main(args: Array<String>) {
        if (args.size > 1) {
            println("Usage: klox [script]")
            exitProcess(0)
        } else if (args.size == 1) {
            runFile(args[0])
        } else {
            runPrompt()
        }
    }

    private fun runFile(path: String) {
        val bytes: ByteArray = Files.readAllBytes(Path(path))
        if (hadError) {
            exitProcess(65)
        }
        if (hadRuntimeError) {
            exitProcess(70)
        }
        run(String(bytes, Charset.defaultCharset()))
    }

    private fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            println("> ")
            val line = reader.readLine()
            if (line.isNullOrBlank()) {
                break
            }
            run(line)
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)
        val statements = parser.parse()

        if (hadError) {
            return
        }
        interpreter.interpret(statements)
    }

    fun error(
        line: Int,
        message: String,
    ) {
        hadError = true
        logger.error(line, message)
    }

    fun error(
        token: Token,
        message: String,
    ) {
        if (token.type == TokenType.EOF) {
            logger.error(token.line, message)
        } else {
            logger.error(token.line, " at ${token.lexeme}", message)
        }
    }

    fun runtimeError(error: RuntimeError) {
        println("${error.message}\n[line ${error.token.line}]")
        hadRuntimeError = true
    }
}
