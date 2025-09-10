class Interpretor : Expr.Visitor<Any?> {
    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG_EQUAL -> {
                !isEqual(left, right)
            }

            TokenType.EQUAL_EQUAL -> {
                isEqual(left, right)
            }

            TokenType.GREATER -> {
                (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                (left as Double) <= (right as Double)
            }

            TokenType.MINUS -> {
                (left as Double) - (right as Double)
            }

            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                }
                if (left is String && right is String) {
                    left + right
                }
                throw IllegalStateException("Unexpected token: $left + $right")
            }

            TokenType.SLASH -> {
                (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                (left as Double) * (right as Double)
            }

            else -> {
                throw IllegalStateException("Unexpected operator ${expr.operator}")
            }
        }
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

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) {
            return true
        }
        if (a == null) {
            return false
        }
        return a == b
    }
}