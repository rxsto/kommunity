package to.rxs.commands.arguments

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.result.ArgumentResult

class SentenceArgument(
    private val surroundedBy: String = """"""", // this is just a string containing a "
    override val example: String = """"this is an example"""",
    override val name: String = "sentence argument"
) : Argument<String, Any?> {

    init {
        require(!surroundedBy.contains("\\s".toRegex())) { "SurroundedBy must not contain whitespace" }
    }

    override suspend fun parse(words: List<String>, fromIndex: Int, context: Any?): ArgumentResult<String> {
        val arguments = words.drop(fromIndex)
        val first = arguments.first()
        if (!first.startsWith(surroundedBy)) return ArgumentResult.Failure(
            "Sentence must start with $surroundedBy",
            fromIndex
        )
        val end = arguments.indexOfFirst { it.endsWith(surroundedBy) }
        val text = arguments.subList(0, end + 1)

        val sentence = text.joinToString(" ")
        return ArgumentResult.Success(sentence.substring(1, sentence.length - 1), text.size)
    }
}
