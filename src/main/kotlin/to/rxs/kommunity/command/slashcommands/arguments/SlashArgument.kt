package to.rxs.kommunity.command.slashcommands.arguments

import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.x.commands.argument.Argument
import java.util.*

interface SlashArgument<T, CONTEXT> : Argument<T, CONTEXT> {
    val description: String
    val required: Boolean
    override val name: String
        get() = name.lowercase(Locale.getDefault())

    /**
     * Applies the argument configuration to the [ApplicationCommandCreateBuilder].
     */
    fun BaseInputChatBuilder.applyArgument()
}
