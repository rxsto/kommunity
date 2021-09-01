package to.rxs.kommunity.command.slashcommands

import dev.kord.core.behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.behavior.interaction.PublicInteractionResponseBehavior
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.command.CommandBuilder
import dev.kord.x.commands.model.metadata.Metadata
import dev.kord.x.commands.model.module.CommandSet
import dev.kord.x.commands.model.module.commands
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun interface AcknowledgingMethod<T : InteractionResponseBehavior> {
    suspend fun acknowledge(interaction: Interaction): T

    companion object Public : AcknowledgingMethod<PublicInteractionResponseBehavior> {
        override suspend fun acknowledge(interaction: Interaction): PublicInteractionResponseBehavior =
            interaction.acknowledgePublic()
    }

    object Ephemeral : AcknowledgingMethod<EphemeralInteractionResponseBehavior> {
        override suspend fun acknowledge(interaction: Interaction): EphemeralInteractionResponseBehavior =
            interaction.acknowledgeEphemeral()
    }
}

@PublishedApi
internal object Ack : Metadata.Key<AcknowledgingMethod<*>>

@Suppress("UNCHECKED_CAST") // it's either set or T is PublicInteractionResponseBehavior
val <T : InteractionResponseBehavior> Command<SlashCommandEvent<out T>>.acknowledgingMethod: AcknowledgingMethod<T>
    get() = (data.metadata[Ack] ?: AcknowledgingMethod.Public) as AcknowledgingMethod<T>

@OptIn(ExperimentalContracts::class)
inline fun <S, A, T : InteractionResponseBehavior> CommandBuilder<S, A, SlashCommandEvent<*>>.acknowledge(
    method: AcknowledgingMethod<T>,
    builder: CommandBuilder<S, A, SlashCommandEvent<T>>.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    metaData[Ack] = method

    @Suppress("UNCHECKED_CAST") // changing the ack method above ensures the correct type
    (this as CommandBuilder<S, A, SlashCommandEvent<T>>).builder()
}

fun <T : InteractionResponseBehavior> command(
    name: String,
    method: AcknowledgingMethod<T>,
    builder: CommandBuilder<InteractionCreateEvent, InteractionCreateEvent, SlashCommandEvent<T>>.() -> Unit
): CommandSet = commands(InteractionContext) {
    command(name) {
        acknowledge(method, builder)
    }
}

fun command(
    name: String,
    builder: CommandBuilder<InteractionCreateEvent, InteractionCreateEvent, SlashCommandEvent<PublicInteractionResponseBehavior>>.() -> Unit
): CommandSet = command(name, AcknowledgingMethod.Public, builder)

fun ephemeralCommand(
    name: String,
    builder: CommandBuilder<InteractionCreateEvent, InteractionCreateEvent, SlashCommandEvent<EphemeralInteractionResponseBehavior>>.() -> Unit
): CommandSet = command(name, AcknowledgingMethod.Ephemeral, builder)
