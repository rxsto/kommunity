package to.rxs.kommunity.command.slashcommands.respond

import dev.kord.core.behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.behavior.interaction.followUpEphemeral
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.modify.MessageModifyBuilder

class EphemeralRespondStrategy(private val delegate: EphemeralInteractionResponseBehavior) : ResponseStrategy {
    override suspend fun respond(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
        delegate.edit(builder)

        return EphemeralEditableResponse()
    }

    override suspend fun followUp(builder: MessageCreateBuilder.() -> Unit): ResponseStrategy.EditableResponse {
        val followUp = delegate.followUpEphemeral(builder)

        return EphemeralFollowupResponse(followUp)
    }

    class EphemeralFollowupResponse(private val delegate: EphemeralFollowupMessage) :
        ResponseStrategy.EditableResponse {
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
            delegate.edit(builder)

            return this
        }
    }

    private inner class EphemeralEditableResponse : ResponseStrategy.EditableResponse {
        override suspend fun edit(builder: MessageModifyBuilder.() -> Unit): ResponseStrategy.EditableResponse {
            delegate.edit(builder)
            return this
        }
    }
}
