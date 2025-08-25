// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val expression = Expr.Binary(
        left = Expr.Unary(
            operator = Token(TokenType.MINUS, "-", null, 1),
            right = Expr.Literal(123)
        ),
        operator = Token(TokenType.STAR, "*", null, 1),
        right = Expr.Grouping(Expr.Literal(45.67))
    )
    println(AstPrinter().print(expression))
}
