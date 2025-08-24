import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.system.exitProcess

object Lox {
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

    private fun run(script: String) {

    }
}