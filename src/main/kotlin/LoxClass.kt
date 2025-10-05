class LoxClass(
    val name: String,
    val methods: Map<String, LoxFunction>,
) : LoxCallable {
    override fun toString(): String = name
    override fun arity(): Int {
        return 0
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance = LoxInstance(this)
        return instance
    }

    fun findMethod(name: String): LoxFunction? {
        return methods[name]
    }
}