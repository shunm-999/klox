import java.util.*

class Resolver(
    private val interpreter: Interpreter
) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {

    private val scopes: Stack<HashMap<String, Boolean>> = Stack()

    override fun visitBlockStmt(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitVarStmt(stmt: Stmt.Var) {
        declare(stmt.token)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.token)
    }

    private fun beginScope() {
        scopes.push(HashMap())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun resolve(statements: List<Stmt>) {
        for (statement in statements) {
            resolve(statement)
        }
    }

    private fun resolve(statement: Stmt) {
        statement.accept(this)
    }

    private fun resolve(expr: Expr) {
        expr.accept(this)
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) {
            return
        }
        val scope = scopes.peek()
        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) {
            return
        }
        val scope = scopes.peek()
        scope[name.lexeme] = true
    }
}