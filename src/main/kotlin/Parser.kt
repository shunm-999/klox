import java.text.ParseException

class Parser(
    private val tokens: List<Token>,
) {
    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt?>()
        while (!isAtEnd()) {
            statements.add(declaration())
        }
        return statements.filterNotNull()
    }

    private fun declaration(): Stmt? {
        try {
            if (match(TokenType.CLASS)) {
                return classDeclaration()
            }
            if (match(TokenType.FUN)) {
                return function("function")
            }
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
        if (match(TokenType.FOR)) {
            return forStatement()
        }
        if (match(TokenType.IF)) {
            return ifStatement()
        }
        if (match(TokenType.PRINT)) {
            return printStatement()
        }
        if (match(TokenType.RETURN)) {
            return returnStatement()
        }
        if (match(TokenType.WHILE)) {
            return whileStatement()
        }
        if (match(TokenType.LEFT_BRACE)) {
            return blockStatement()
        }
        return expressionStatement()
    }

    private fun classDeclaration(): Stmt {
        val name: Token = consume(TokenType.IDENTIFIER, "Expect class name.")
        consume(TokenType.LEFT_BRACE, "Expect '{' before class body.")

        val methods: List<Stmt.Function> = buildList {
            while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                add(function("method"))
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.")

        return Stmt.Class(
            name = name,
            methods = methods,
        )
    }

    private fun function(kind: String): Stmt.Function {
        val name: Token = consume(TokenType.IDENTIFIER, "Expect $kind name")
        consume(TokenType.LEFT_PAREN, "Expect '(' after $kind name.")

        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size > 255) {
                    error(peek(), "Cannot have more than 255 parameters")
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name"))
            } while (match(TokenType.COMMA))
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect '{' before $kind body.")

        val body = block()
        return Stmt.Function(
            name = name,
            params = parameters,
            body = body,
        )
    }

    private fun varDeclaration(): Stmt {
        val name: Token = consume(TokenType.IDENTIFIER, "Expect variable name.")

        val initializer: Expr? =
            if (match(TokenType.EQUAL)) {
                expression()
            } else {
                null
            }
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Stmt.Var(name, initializer)
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after for.")

        val initializer: Stmt? =
            if (match(TokenType.SEMICOLON)) {
                null
            } else if (match(TokenType.VAR)) {
                varDeclaration()
            } else {
                expressionStatement()
            }

        var condition: Expr? =
            if (!check(TokenType.SEMICOLON)) {
                expression()
            } else {
                null
            }
        consume(TokenType.SEMICOLON, "Expect ';' loop condition.")

        val increment: Expr? =
            if (!check(TokenType.RIGHT_PAREN)) {
                expression()
            } else {
                null
            }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

        var body = statement()

        if (increment != null) {
            body =
                Stmt.Block(
                    listOf(
                        body,
                        Stmt.Expression(increment),
                    ),
                )
        }
        if (condition == null) {
            condition = Expr.Literal(true)
        }
        body = Stmt.While(condition, body)

        if (initializer != null) {
            body =
                Stmt.Block(
                    listOf(
                        initializer,
                        body,
                    ),
                )
        }

        return body
    }

    private fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.")
        val condition: Expr = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")

        val thenBranch: Stmt = statement()
        val elseBranch: Stmt? =
            if (match(TokenType.ELSE)) {
                statement()
            } else {
                null
            }

        return Stmt.If(
            condition = condition,
            thenBranch = thenBranch,
            elseBranch = elseBranch,
        )
    }

    private fun printStatement(): Stmt {
        val value: Expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Print(value)
    }

    private fun returnStatement(): Stmt {
        val keyword = previous()

        val value: Expr? =
            if (check(TokenType.SEMICOLON)) {
                null
            } else {
                expression()
            }

        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Return(keyword, value)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.")
        val condition: Expr = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
        val body: Stmt = statement()

        return Stmt.While(condition, body)
    }

    private fun expressionStatement(): Stmt {
        val value: Expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Expression(value)
    }

    private fun blockStatement(): Stmt {
        val statements =
            buildList {
                while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                    add(declaration())
                }
            }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return Stmt.Block(statements.filterNotNull())
    }

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr: Expr = or()

        if (match(TokenType.EQUAL)) {
            val equals: Token = previous()
            val value: Expr = assignment()

            if (expr is Expr.Variable) {
                val name: Token = expr.name
                return Expr.Assign(name, value)
            } else if (expr is Expr.Get) {
                val get: Expr.Get = expr
                return Expr.Set(
                    instance = get.instance,
                    name = get.name,
                    value = value
                )
            }

            error(equals, "Invalid assignment in expression.")
        }

        return expr
    }

    private fun or(): Expr {
        var expr: Expr = and()

        while (match(TokenType.OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        var expr: Expr = equality()

        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr: Expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right: Expr = comparison()
            expr =
                Expr.Binary(
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
            expr =
                Expr.Binary(
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
            expr =
                Expr.Binary(
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
            expr =
                Expr.Binary(
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

        return call()
    }

    private fun block(): List<Stmt> {
        val statements =
            buildList {
                while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                    add(declaration())
                }
            }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements.filterNotNull()
    }

    private fun call(): Expr {
        var expr: Expr = primary()

        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else if (match(TokenType.DOT)) {
                val name: Token = consume(TokenType.IDENTIFIER, "Expect property name after '.'.")
                expr = Expr.Get(instance = expr, name)
            } else {
                break
            }
        }

        return expr
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
        if (match(TokenType.THIS)) {
            return Expr.This(previous())
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

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type == tokenType
    }

    private fun previous(): Token = tokens[current - 1]

    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    private fun peek(): Token = tokens[current]

    private fun consume(
        type: TokenType,
        errorMessage: String,
    ): Token {
        if (check(type)) {
            return advance()
        }
        throw error(peek(), errorMessage)
    }

    private fun error(
        token: Token,
        errorMessage: String,
    ): ParseException {
        Lox.error(token, errorMessage)
        return ParseException(errorMessage, token.line)
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return
            }
            if (peek().type in
                listOf(
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

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()

        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size > 255) {
                    error(peek(), "Cannot have more than 255 arguments.")
                }
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }

        val paren: Token = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")

        return Expr.Call(
            callee = callee,
            paren = paren,
            arguments = arguments,
        )
    }
}
