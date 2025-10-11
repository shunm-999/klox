class LoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
) : LoxCallable {

    override fun arity(): Int = declaration.params.size

    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any? {
        val environment = Environment(closure)

        for ((index, param) in declaration.params.withIndex()) {
            environment.define(param.lexeme, arguments[index])
        }
        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }
        if (isInitializer) {
            return closure.getAt(
                0, Token(
                    type = TokenType.IDENTIFIER,
                    lexeme = "this",
                    literal = null,
                    line = 1
                )
            )
        }
        return null
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LoxFunction(
            declaration = declaration,
            closure = environment,
            isInitializer = isInitializer
        )
    }

    override fun toString(): String = "<fn ${declaration.name}>"
}
