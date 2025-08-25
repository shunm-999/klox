sealed interface Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitUnaryExpr(expr: Unary): R
    }

    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr

    data class Grouping(val expression: Expr) : Expr

    data class Literal(val value: Object) : Expr

    data class Unary(val operator: Token, val right: Expr) : Expr

}
