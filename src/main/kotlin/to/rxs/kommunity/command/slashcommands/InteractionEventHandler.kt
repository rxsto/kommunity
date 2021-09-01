package to.rxs.kommunity.command.slashcommands

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.x.commands.argument.Argument
import dev.kord.x.commands.argument.result.ArgumentResult
import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.processor.ArgumentsResult
import dev.kord.x.commands.model.processor.CommandProcessor
import dev.kord.x.commands.model.processor.ErrorHandler
import dev.kord.x.commands.model.processor.EventHandler
import mu.KotlinLogging
import to.rxs.kommunity.command.slashcommands.respond.ResponseStrategy
import to.rxs.kommunity.command.slashcommands.respond.asRespondStrategy
import java.util.*

private val LOG = KotlinLogging.logger { }

data class InteractionErrorEvent constructor(
    val interaction: ChatInputCommandInteraction,
    val strategy: ResponseStrategy,
    override val kord: Kord,
    override val shard: Int,
) : ResponseStrategy by strategy, Event

class InteractionEventHandler(
    private val errorHandler: ErrorHandler<InteractionErrorEvent, InteractionCreateEvent, SlashCommandEvent<*>>
) : EventHandler<InteractionCreateEvent> {
    override val context: InteractionContext = InteractionContext

    override suspend fun CommandProcessor.onEvent(event: InteractionCreateEvent) {
        suspend fun <T : InteractionResponseBehavior> Command<SlashCommandEvent<out T>>.run(interaction: GuildChatInputCommandInteraction) {
            val ack = acknowledgingMethod.acknowledge(interaction)
            val responseStrategy = ack.asRespondStrategy()

            val errorEvent = InteractionErrorEvent(interaction, responseStrategy, event.kord, event.shard)
            val commandEvent =
                SlashCommandEvent(
                    this,
                    commands,
                    this@onEvent,
                    ack,
                    responseStrategy,
                    interaction.command,
                    event.kord,
                    event.shard,
                    interaction
                )

            val preconditions = getPreconditions(context)

            val passed = preconditions.sortedByDescending { it.priority }.all { it(commandEvent) }

            if (!passed) return

            execute(commandEvent, event, this@onEvent, errorEvent)
        }


        val guildInteraction = event.interaction as? GuildChatInputCommandInteraction ?: return
        val filters = getFilters(context)
        if (!filters.all { it(event) }) return
        val commandName = guildInteraction.name
        val foundCommand = getCommand(context, commandName) ?: return


        foundCommand.run(guildInteraction)
    }

    private suspend fun <T : InteractionResponseBehavior> Command<SlashCommandEvent<out T>>.execute(
        commandEvent: SlashCommandEvent<T>,
        event: InteractionCreateEvent,
        processor: CommandProcessor,
        errorEvent: InteractionErrorEvent
    ) {
        @Suppress("UNCHECKED_CAST")
        val arguments =
            arguments as List<Argument<*, InteractionCreateEvent>>

        val result =
            parseArguments(commandEvent.interactionCommand, arguments, event)

        val invocationText by lazy { errorEvent.interaction.buildInvocationString() }

        val (items) = when (result) {
            is ArgumentsResult.Success -> result
            is ArgumentsResult.Failure -> return with(errorHandler) {
                @Suppress("UNCHECKED_CAST") // out T or * should be the same
                val rejection = ErrorHandler.RejectedArgument(
                    this@execute,
                    errorEvent,
                    invocationText,
                    result.atChar,
                    result.argument,
                    result.failure.reason
                ) as ErrorHandler.RejectedArgument<InteractionErrorEvent, InteractionCreateEvent, SlashCommandEvent<*>>
                processor.rejectArgument(rejection)
            }
            is ArgumentsResult.TooManyWords -> error("Discord bunged up")
        }


        try {
            invoke(commandEvent, items)
        } catch (exception: Exception) {
            LOG.catching(exception)

            @Suppress("UNCHECKED_CAST") // out T or * should be the same
            val command = this as Command<SlashCommandEvent<*>>
            with(errorHandler) { processor.exceptionThrown(errorEvent, command, exception) }
        }
    }

    private suspend fun parseArguments(
        command: InteractionCommand,
        arguments: List<Argument<*, InteractionCreateEvent>>,
        event: InteractionCreateEvent,
    ): ArgumentsResult<InteractionCreateEvent> {
        val items = mutableListOf<Any?>()
        var indexTrim = 2 + command.rootName.length // /<command>
        arguments.forEachIndexed { index, argument ->
            val option = command.options[argument.name.lowercase(Locale.getDefault())]
            // each argument is prefix by " <name>:"
            indexTrim += argument.name.length + 2
            val argumentValue = option?.value
            val argumentText = option.stringify()
            when (val result = argument.parse(argumentText, 0, event)) {
                is ArgumentResult.Success -> {
                    val item = result.item
                    items += if (item is KordObject && argumentText.isNotBlank()) {
                        argumentValue
                    } else {
                        item
                    }
                    indexTrim += result.newIndex + 1 // space after the argument
                }
                is ArgumentResult.Failure -> return ArgumentsResult.Failure(
                    event,
                    result,
                    argument,
                    arguments,
                    index,
                    argumentText,
                    result.atChar + indexTrim
                )
            }
        }

        return ArgumentsResult.Success(items)
    }

    private fun OptionValue<*>?.stringify(): String {
        return when (val argumentValue = this?.value) {
            null -> ""
            is Member -> argumentValue.mention
            is User -> argumentValue.mention
            is ResolvedChannel -> argumentValue.mention
            is Role -> argumentValue.mention
            else -> argumentValue.toString()
        }
    }

    private fun ChatInputCommandInteraction.buildInvocationString(): String {
        fun Any?.stringify(): String =
            when (this) {
                is Member -> id.asString
                is User -> id.asString
                is ResolvedChannel -> id.asString
                is Role -> id.asString
                else -> toString()
            }

        return buildString {
            append('/')
            append(name)

            command.options.forEach { (name, option) ->
                append(' ')
                append(name)
                append(':')
                append(' ')

                append(option.value.stringify())
            }
        }
    }
}
