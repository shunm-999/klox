sealed interface Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R

        fun visitBinaryExpr(expr: Binary): R

        fun visitCallExpr(expr: Call): R

        fun visitGroupingExpr(expr: Grouping): R

        fun visitLiteralExpr(expr: Literal): R

        fun visitLogicalExpr(expr: Logical): R

        fun visitUnaryExpr(expr: Unary): R

        fun visitVariableExpr(expr: Variable): R
    }

    fun <R> accept(visitor: Visitor<R>): R

    data class Assign(
        val name: Token,
        val value: Expr,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitAssignExpr(this)
    }

    data class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBinaryExpr(this)
    }

    data class Call(
        val callee: Expr,
        val paren: Token,
        val arguments: List<Expr>,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitCallExpr(this)
    }

    data class Grouping(
        val expression: Expr,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGroupingExpr(this)
    }

    data class Literal(
        val value: Any?,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitLiteralExpr(this)
    }

    data class Logical(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitLogicalExpr(this)
    }

    data class Unary(
        val operator: Token,
        val right: Expr,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitUnaryExpr(this)
    }

    data class Variable(
        val name: Token,
    ) : Expr {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVariableExpr(this)
    }
}

sealed interface Stmt {
    interface Visitor<R> {
        fun visitBlockStmt(stmt: Block): R

        fun visitExpressionStmt(stmt: Expression): R

        fun visitFunctionStmt(stmt: Function): R

        fun visitIfStmt(stmt: If): R

        fun visitPrintStmt(stmt: Print): R

        fun visitReturnStmt(stmt: Return): R

        fun visitVarStmt(stmt: Var): R

        fun visitWhileStmt(stmt: While): R
    }

    fun <R> accept(visitor: Visitor<R>): R

    data class Block(
        val statements: List<Stmt>,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBlockStmt(this)
    }

    data class Expression(
        val expression: Expr,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitExpressionStmt(this)
    }

    data class Function(
        val name: Token,
        val params: List<Token>,
        val body: List<Stmt>,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitFunctionStmt(this)
    }

    data class If(
        val condition: Expr,
        val thenBranch: Stmt,
        val elseBranch: Stmt?,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitIfStmt(this)
    }

    data class Print(
        val expression: Expr,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitPrintStmt(this)
    }

    data class Return(
        val keyword: Token,
        val value: Expr?,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitReturnStmt(this)
    }

    data class Var(
        val token: Token,
        val initializer: Expr?,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVarStmt(this)
    }

    data class While(
        val condition: Expr,
        val body: Stmt,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitWhileStmt(this)
    }
}
