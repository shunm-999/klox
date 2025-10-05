class LoxInstance(
    private val klass: LoxClass
) {

    private val fields: Map<String, Any> = HashMap()

    override fun toString(): String {
        return "${klass.name} instance"
    }

    fun get(name: Token): Any {
        return fields[name.lexeme] ?: throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }
}