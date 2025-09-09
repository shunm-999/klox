class Logger {
    fun error(line: Int, message: String) {
        error(line, "", message)
    }

    fun error(line: Int, where: String, message: String) {
        println("[line $line] $where: $message")
    }
}
