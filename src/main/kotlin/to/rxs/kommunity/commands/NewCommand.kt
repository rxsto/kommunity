@file:AutoWired

package to.rxs.kommunity.commands

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.channel
import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.SingleWordArgument
import dev.kord.x.commands.argument.extension.named
import dev.kord.x.commands.argument.result.WordResult
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.model.command.invoke
import io.ktor.client.request.*
import to.rxs.kommunity.command.slashcommands.SlashArgument
import to.rxs.kommunity.command.slashcommands.arguments.asAnySlashArgument
import to.rxs.kommunity.command.slashcommands.arguments.asSlashArgument
import to.rxs.kommunity.command.slashcommands.command
import to.rxs.kommunity.command.slashcommands.description
import to.rxs.kommunity.commands.arguments.HastebinArgument
import to.rxs.kommunity.commands.arguments.URLArgument
import to.rxs.kommunity.entities.Permission
import to.rxs.kommunity.util.httpClient
import to.rxs.kommunity.util.withPermission

@Suppress("UNCHECKED_CAST")
private object ChannelArgument :
    SingleWordArgument<TextChannel, InteractionCreateEvent>(),
    SlashArgument<TextChannel, InteractionCreateEvent> {
    private val mentionRegex = Regex("""^<#\d+>$""")
    override val name: String = "channel"
    override val required: Boolean = true
    override val description: String = "The new channel"

    override suspend fun parse(word: String, context: InteractionCreateEvent): WordResult<TextChannel> {
        val number = word.toLongOrNull()
        val snowflake = when {
            number != null -> Snowflake(number)
            word.matches(mentionRegex) -> Snowflake(
                word.removeSuffix(">").dropWhile { !it.isDigit() }
            )
            else -> return failure("Expected mention.")
        }

        return when (val channel = context.kord.getChannelOf<TextChannel>(snowflake)) {
            null -> failure("Channel not found.")
            else -> success(channel)
        }
    }

    override fun BaseInputChatBuilder.applyArgument() {
        channel(name, description) {
            required = true
        }
    }
}

private val TitleArgument = StringArgument.named("title").asSlashArgument("The title of the welcome channel message")
private val AvatarArgument =
    URLArgument.named("avatar").asAnySlashArgument("The URL to the avatar of the welcome embed")
private val FooterArgument = StringArgument.named("footer").asSlashArgument("The footer of the welcome channel message")
private val DescriptionArgument =
    HastebinArgument.named("description").asAnySlashArgument("Url to a haste containing the description")

@ModuleName("Admin")
@AutoWired
fun newCommand() =
    command("new-welcomechannel") {
        description("Creates a new welcome channel")

        withPermission(Permission.ADMIN)

        invoke(
            ChannelArgument,
            TitleArgument,
            AvatarArgument,
            FooterArgument,
            DescriptionArgument
        ) { channel, titleSentence, avatarUrl, footerSentence, hastebinMatch ->
            val (_, hastebin, code) = hastebinMatch.groupValues
            val rawUrl = "https://$hastebin/raw/$code"
            val content = httpClient.get<String>(rawUrl)
            channel.createEmbed {
                title = titleSentence
                description = content
                footer {
                    text = footerSentence
                    icon = avatarUrl.value
                }
            }.pin()
        }
    }
