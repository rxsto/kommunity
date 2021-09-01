package to.rxs.kommunity.command.slashcommands

import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.x.commands.kord.model.processor.KordProcessorBuilder
import dev.kord.x.commands.model.processor.ProcessorContext

object InteractionContext :
    ProcessorContext<InteractionCreateEvent, InteractionCreateEvent, SlashCommandEvent<*>>

fun KordProcessorBuilder.slashCommands() {
    eventSources.clear()
    eventSources += InteractionEventSource(kord)

    eventHandlers[InteractionContext] = InteractionEventHandler(InteractionErrorHandler)
}
