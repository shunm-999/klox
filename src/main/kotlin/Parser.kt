class Parser(
    private val tokens: List<Token>
) {

    private var current = 0

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
                right = right
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
                TokenType.LESS_EQUAL
            )
        ) {
            val operator = previous()
            val right: Expr = term()
            expr = Expr.Binary(
                left = expr,
                operator = operator,
                right = right
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
                right = right
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
                right = right
            )
        }

        return expr
    }

    private fun unary(): Expr {
        TODO()
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
}