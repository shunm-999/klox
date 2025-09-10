class Interpretor : Expr.Visitor<Any?> {
    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> {
                return !isTruthy(right)
            }

            TokenType.MINUS -> {
                -(right as Double)
            }

            else -> {
                throw IllegalStateException("Unexpected operator ${expr.operator}")
            }
        }
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(target: Any?): Boolean {
        if (target == null) {
            return false
        }
        if (target is Boolean) {
            return target
        }
        return true
    }
}