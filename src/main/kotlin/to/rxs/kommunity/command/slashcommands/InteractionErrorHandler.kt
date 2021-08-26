package to.rxs.kommunity.command.slashcommands

import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.x.commands.model.processor.CommandProcessor
import dev.kord.x.commands.model.processor.ErrorHandler
import mu.KotlinLogging

private val LOG = KotlinLogging.logger { }

object InteractionErrorHandler : ErrorHandler<InteractionErrorEvent, InteractionCreateEvent, SlashCommandEvent<*>> {
    private const val backtick = "`"
    private const val backtickEscape = "\u200E`"

    @Suppress("ParameterListWrapping")
    override suspend fun CommandProcessor.rejectArgument(
        rejection: ErrorHandler.RejectedArgument<InteractionErrorEvent, InteractionCreateEvent, SlashCommandEvent<*>>
    ) {
        with(rejection) {
            respondError(
                event,
                eventText,
                atChar,
                message
            )
        }
    }

    override suspend fun CommandProcessor.exceptionThrown(
        event: InteractionErrorEvent,
        command: dev.kord.x.commands.model.command.Command<SlashCommandEvent<*>>,
        exception: Exception,
    ) {
        LOG.error(exception) { "Could not execute command" }
        event.respond("An error occurred, this should have been logged automatically")
    }

    private suspend inline fun respondError(
        event: InteractionErrorEvent,
        text: String,
        characterIndex: Int,
        message: String,
    ) {
        event.respond(
            """
            <|>```
            <|>${text.replace(backtick, backtickEscape)}
            <|>${"-".repeat(characterIndex)}^ ${message.replace(backtick, backtickEscape)}
            <|>```
            """.trimMargin("<|>").trim()
        )
    }
}
