package to.rxs.kommunity.listeners

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import mu.KotlinLogging
import to.rxs.kommunity.commands.slash.impl.OptSlashCommand

private val log = KotlinLogging.logger {}

@OptIn(KordPreview::class)
fun Kord.interactionListener() = on<InteractionCreateEvent> {
    when (interaction.command.rootName) {
        "opt" -> OptSlashCommand.handle(kord, interaction)
    }
}
