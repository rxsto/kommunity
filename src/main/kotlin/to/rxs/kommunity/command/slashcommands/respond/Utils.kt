package to.rxs.kommunity.command.slashcommands.respond

import dev.kord.core.behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.behavior.interaction.PublicInteractionResponseBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.modify.MessageModifyBuilder

fun InteractionResponseBehavior.asRespondStrategy(): ResponseStrategy = when (this) {
    is EphemeralInteractionResponseBehavior -> EphemeralRespondStrategy(this)
    is PublicInteractionResponseBehavior -> PublicResponseStrategy(this)
    else -> error("Unknown response behavior: $this")
}

fun MessageCreateBuilder.embed(embedBuilder: EmbedBuilder) {
    embeds.clear()
    embeds += embedBuilder
}

fun MessageModifyBuilder.embed(embedBuilder: EmbedBuilder) {
    embeds = mutableListOf(embedBuilder)
}
