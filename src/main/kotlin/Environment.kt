data class Environment(
    private val enclosing: Environment? = null,
) {
    private val values: HashMap<String, Any?> = hashMapOf()

    fun define(
        name: String,
        value: Any?,
    ) {
        values[name] = value
    }

    fun assign(
        name: Token,
        value: Any?,
    ) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }
        if (enclosing != null) {
            enclosing.assign(name, value)
        } else {
            throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }
    }

    fun assignAt(distance: Int, name: Token, value: Any?) {
        ancestor(distance).values[name.lexeme] = value
    }

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }
        if (enclosing != null) {
            return enclosing.get(name)
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun getAt(distance: Int, name: Token): Any? = ancestor(distance).values[name.lexeme]

    fun ancestor(distance: Int): Environment {
        var environment = this
        for (i in 0 until distance) {
            environment = environment.enclosing ?: throw IllegalStateException("No enclosing environment.")
        }
        return environment
    }
}
