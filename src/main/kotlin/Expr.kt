sealed interface Expr {
    data class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr
}
