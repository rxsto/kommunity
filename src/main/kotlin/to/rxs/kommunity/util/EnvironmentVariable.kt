package to.rxs.kommunity.util

import io.github.cdimascio.dotenv.dotenv
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val dotenv = dotenv()

/**
 * Helper class that allows you to specify a [prefix] for your whole config.
 *
 * Is intended to be used via composition or via inheritance
 */
open class EnvironmentConfig(private val prefix: String) {

    /**
     * @see getEnv
     */
    protected fun getEnv(default: String? = null): ReadOnlyProperty<Any, String> = getEnv(prefix, default)

    /**
     * @see getEnv
     */
    protected fun <T> getEnv(default: T? = null, transform: (String) -> T?): ReadOnlyProperty<Any, T> =
        getEnv(prefix, default, transform)
}

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if the found variable is empty or invalid
 */
fun getEnv(prefix: String? = null, default: String? = null): ReadOnlyProperty<Any, String> =
    EnvironmentVariable(prefix, { it }, default)

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if the found variable is empty or invalid.
 *
 * The variable is transformed to [T] by [transform]
 */
fun <T> getEnv(prefix: String? = null, default: T? = null, transform: (String) -> T?): ReadOnlyProperty<Any, T> =
    EnvironmentVariable(prefix, transform, default)

internal class EnvironmentVariable<T>(
    private val prefix: String?,
    private val transform: (String) -> T?,
    private val default: T?
) : ReadOnlyProperty<Any, T> {

    @Volatile private var _value: T? = null

    private val KProperty<*>.prefixedName: String
        get() = prefix?.let { it + name } ?: name

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val _v1 = _value
        if (_v1 != null) {
            return _v1
        }

        return synchronized(this) {
            val _v2 = _value
            if (_v2 != null) {
                _v2
            } else {
                val typedValue = getEnv(property, default, transform)
                _value = typedValue
                typedValue
            }
        }
    }

    private fun <T> getEnv(property: KProperty<*>, default: T? = null, transform: (String) -> T?): T =
        dotenv[property.prefixedName]?.let(transform) ?: default ?: missing(property.prefixedName)

    private fun missing(name: String): Nothing = error("Missing env variable: $name")
}
