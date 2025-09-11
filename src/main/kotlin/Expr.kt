sealed interface Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R

        fun visitBinaryExpr(expr: Binary): R

        fun visitGroupingExpr(expr: Grouping): R

        fun visitLiteralExpr(expr: Literal): R

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

        fun visitIfStmt(stmt: If): R

        fun visitPrintStmt(stmt: Print): R

        fun visitVarStmt(stmt: Var): R
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

    data class Var(
        val token: Token,
        val initializer: Expr?,
    ) : Stmt {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVarStmt(this)
    }
}
