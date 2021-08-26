package to.rxs.kommunity.command.slashcommands.respond

import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.modify.MessageModifyBuilder
import dev.kord.rest.builder.message.modify.embed
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ResponseStrategy {

    suspend fun respond(builder: MessageModifyBuilder.() -> Unit): EditableResponse
    suspend fun respond(message: String): EditableResponse = respond {
        content = message
    }


    suspend fun followUp(builder: MessageCreateBuilder.() -> Unit): EditableResponse

    interface EditableResponse {
        suspend fun edit(builder: MessageModifyBuilder.() -> Unit): EditableResponse

        suspend fun edit(message: String): EditableResponse = edit {
            content = message
        }
    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ResponseStrategy.respondEmbed(crossinline embedBuilder: EmbedBuilder.() -> Unit): ResponseStrategy.EditableResponse {
    contract {
        callsInPlace(embedBuilder, InvocationKind.EXACTLY_ONCE)
    }

    return respond {
        embed(embedBuilder)
    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ResponseStrategy.EditableResponse.editEmbed(crossinline embedBuilder: EmbedBuilder.() -> Unit): ResponseStrategy.EditableResponse {
    contract {
        callsInPlace(embedBuilder, InvocationKind.EXACTLY_ONCE)
    }

    return  edit {
        embed(embedBuilder)
    }
}
