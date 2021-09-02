package to.rxs.kommunity.command.slashcommands

import dev.kord.core.Kord
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.x.commands.model.processor.EventSource
import dev.kord.x.commands.model.processor.ProcessorContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance

class InteractionEventSource(private val kord: Kord) : EventSource<InteractionCreateEvent> {
    override val context: ProcessorContext<InteractionCreateEvent, *, *>
        get() = InteractionContext
    override val events: Flow<InteractionCreateEvent>
        get() = kord.events.buffer(Channel.UNLIMITED).filterIsInstance()
}
