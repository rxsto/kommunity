package to.rxs.commands.arguments

import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import com.gitlab.kordlib.kordx.commands.argument.result.ArgumentResult

class RegexArgument(override val example: String, override val name: String, private val pattern: Regex) : SingleWordArgument<MatchResult, Any?>() {

    override suspend fun parse(word: String, context: Any?): ArgumentResult<MatchResult> {
        val match = pattern.matchEntire(word) ?: return failure("Argument does not match regex")
        return success(match)
    }
}
