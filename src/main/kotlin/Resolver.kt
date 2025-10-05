import java.util.*

class Resolver(
    private val interpreter: Interpreter
) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {

    private val scopes: Stack<HashMap<String, Boolean>> = Stack()
    private var currentFunction: FunctionType = FunctionType.NONE

    override fun visitBlockStmt(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        declare(stmt.name)
        define(stmt.name)

        for (method in stmt.methods) {
            resolveFunction(method, FunctionType.METHOD)
        }
    }

    override fun visitVarStmt(stmt: Stmt.Var) {
        declare(stmt.token)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.token)
    }

    override fun visitVariableExpr(expr: Expr.Variable) {
        if (scopes.isNotEmpty()) {
            val definition = scopes.peek().get(expr.name.lexeme)
            if (definition != null && !definition) {
                Lox.error(
                    expr.name,
                    "Can't read local variable in its own initializer."
                )
            }
        }
        resolveLocal(expr, expr.name)
    }

    override fun visitAssignExpr(expr: Expr.Assign) {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        declare(stmt.name)
        define(stmt.name)

        resolveFunction(stmt, FunctionType.FUNCTION)
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        resolve(stmt.expression)
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch)
        }
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        resolve(stmt.expression)
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.")
        }

        if (stmt.value != null) {
            resolve(stmt.value)
        }
    }

    override fun visitWhileStmt(stmt: Stmt.While) {
        resolve(stmt.condition)
        resolve(stmt.body)
    }

    override fun visitBinaryExpr(expr: Expr.Binary) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call) {
        resolve(expr.callee)

        for (argument in expr.arguments) {
            resolve(argument)
        }
    }

    override fun visitGetExpr(expr: Expr.Get) {
        resolve(expr.instance)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) {
        resolve(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal) {
        // nop
    }

    override fun visitLogicalExpr(expr: Expr.Logical) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set) {
        resolve(expr.value)
        resolve(expr.instance)
    }

    override fun visitUnaryExpr(expr: Expr.Unary) {
        resolve(expr.right)
    }

    private fun beginScope() {
        scopes.push(HashMap())
    }

    private fun endScope() {
        scopes.pop()
    }

    fun resolve(statements: List<Stmt>) {
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

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.size - 1 downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
            }
        }
    }

    private fun resolveFunction(function: Stmt.Function, type: FunctionType) {

        val enclosingFunction = currentFunction
        currentFunction = type

        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()

        currentFunction = enclosingFunction
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) {
            return
        }
        val scope = scopes.peek()
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already declared in its own initializer.")
        }
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