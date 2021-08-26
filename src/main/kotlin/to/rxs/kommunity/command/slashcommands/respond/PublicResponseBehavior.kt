package to.rxs.kommunity.command.slashcommands.respond

import dev.kord.core.behavior.interaction.PublicInteractionResponseBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.modify.MessageModifyBuilder

class PublicResponseStrategy(private val delegate: PublicInteractionResponseBehavior) : ResponseStrategy {
    override suspend fun respond(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
        delegate.edit(builder)

        return PublicEditableResponse()
    }

    override suspend fun followUp(builder: MessageCreateBuilder.() -> Unit): ResponseStrategy.EditableResponse {
        val followUp = delegate.followUp(builder)

        return PublicFollowupResponse(followUp)
    }

    private class PublicFollowupResponse(private val delegate: PublicFollowupMessage) : ResponseStrategy.EditableResponse{
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
            delegate.edit(builder)

            return this
        }
    }

    private inner class PublicEditableResponse : ResponseStrategy.EditableResponse {
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
            delegate.edit(builder)
            return this
        }
    }
}
