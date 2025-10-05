class LoxInstance(
    private val klass: LoxClass
) {

    private val fields: HashMap<String, Any?> = HashMap()

    override fun toString(): String {
        return "${klass.name} instance"
    }

    fun get(name: Token): Any {
        val field = fields[name.lexeme]
        if (field != null) {
            return field
        }

        val method = klass.findMethod(name.lexeme)
        if (method != null) {
            return method.bind(this)
        }
        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }
}