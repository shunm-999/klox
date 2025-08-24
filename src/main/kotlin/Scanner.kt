class Scanner(private val source: String) {
    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1

    private val tokens: MutableList<Token> = mutableListOf()

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            else -> {
                Lox.error(line, "Unexpected character '$c'")
            }
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) {
            return false
        }
        if (source[current] != expected) {
            return false
        }
        current++
        return true
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        val token = Token(
            type = type,
            lexeme = text,
            literal = literal,
            line = line,
        )
        tokens.add(token)
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }
}
