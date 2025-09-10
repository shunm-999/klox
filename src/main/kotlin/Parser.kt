import java.text.ParseException

class Parser(
    private val tokens: List<Token>,
) {

    private var current = 0

//    fun parse(): Expr? = try {
//        expression()
//    } catch (_: ParseException) {
//        null
//    }

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt?>()
        while (!isAtEnd()) {
            statements.add(declaration())
        }
        return statements.filterNotNull()
    }

    private fun declaration(): Stmt? {
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration()
            }
            return statement()
        } catch (_: Exception) {
            synchronize()
            return null
        }
    }

    private fun statement(): Stmt {
        if (match(TokenType.PRINT)) {
            return printStatement()
        }
        return expressionStatement()
    }

    private fun varDeclaration(): Stmt {
        val name: Token = consume(TokenType.IDENTIFIER, "Expect variable name.")

        val initializer: Expr = if (match(TokenType.EQUAL)) {
            expression()
        } else {
            throw ParseException("Expect variable name expected.", name.line)
        }
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Stmt.Var(name, initializer)
    }

    private fun printStatement(): Stmt {
        val value: Expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Print(value)
    }

    private fun expressionStatement(): Stmt {
        val value: Expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Expression(value)
    }

    private fun expression(): Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr: Expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right: Expr = comparison()
            expr = Expr.Binary(
                left = expr,
                operator = operator,
                right = right,
            )
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr: Expr = term()

        while (
            match(
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL,
            )
        ) {
            val operator = previous()
            val right: Expr = term()
            expr = Expr.Binary(
                left = expr,
                operator = operator,
                right = right,
            )
        }

        return expr
    }

    private fun term(): Expr {
        var expr: Expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right: Expr = factor()
            expr = Expr.Binary(
                left = expr,
                operator = operator,
                right = right,
            )
        }

        return expr
    }

    private fun factor(): Expr {
        var expr: Expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right: Expr = unary()
            expr = Expr.Binary(
                left = expr,
                operator = operator,
                right = right,
            )
        }

        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right: Expr = unary()
            return Expr.Unary(
                operator = operator,
                right = right,
            )
        }

        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) {
            return Expr.Literal(false)
        }
        if (match(TokenType.TRUE)) {
            return Expr.Literal(true)
        }
        if (match(TokenType.NIL)) {
            return Expr.Literal(null)
        }

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr: Expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }

    private fun match(vararg tokenTypes: TokenType): Boolean {
        for (type in tokenTypes) {
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type == tokenType
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun consume(type: TokenType, errorMessage: String): Token {
        if (check(type)) {
            return advance()
        }
        throw error(peek(), errorMessage)
    }

    private fun error(token: Token, errorMessage: String): ParseException {
        Lox.error(token, errorMessage)
        return ParseException(errorMessage, token.line)
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return
            }
            if (peek().type in listOf(
                    TokenType.CLASS,
                    TokenType.FUN,
                    TokenType.VAR,
                    TokenType.FOR,
                    TokenType.IF,
                    TokenType.WHILE,
                    TokenType.PRINT,
                    TokenType.RETURN,
                )
            ) {
                return
            }

            advance()
        }
    }
}
