package to.rxs.kommunity.listeners

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import to.rxs.kommunity.Config

fun Kord.selfMentionListener() = on<MessageCreateEvent> {
    if (message.content.matches("<@!?${kord.selfId.asString}>".toRegex())) {
        message.channel.createEmbed {
            description = "Hey, I'm **${kord.getSelf().username}** and I manage this server. My most important command is `${Config.PREFIX}opt` at the moment."
            footer {
                text = "(WIP) - Slash commands planned"
            }
        }
    }
}
