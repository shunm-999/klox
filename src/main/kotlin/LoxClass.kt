class LoxClass(
    val name: String,
) : LoxCallable {
    override fun toString(): String = name
    override fun arity(): Int {
        return 0
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance: LoxInstance = LoxInstance(this)
        return instance
    }
}