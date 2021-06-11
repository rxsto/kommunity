package to.rxs.kommunity.commands.message.arguments

import dev.kord.x.commands.argument.SingleWordArgument
import dev.kord.x.commands.argument.result.WordResult

class RegexArgument(override val name: String, private val pattern: Regex) : SingleWordArgument<MatchResult, Any?>() {
    override suspend fun parse(word: String, context: Any?): WordResult<MatchResult> {
        val match = pattern.matchEntire(word) ?: return failure("Argument does not match regex")
        return success(match)
    }
}
