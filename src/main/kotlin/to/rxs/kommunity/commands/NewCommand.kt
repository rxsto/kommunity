@file:AutoWired

package to.rxs.kommunity.commands

import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.text.QuotedArgument
import dev.kord.x.commands.kord.argument.TextChannelArgument
import dev.kord.x.commands.kord.module.module
import dev.kord.x.commands.model.command.invoke
import io.ktor.client.request.*
import to.rxs.kommunity.commands.arguments.HastebinArgument
import to.rxs.kommunity.commands.arguments.URLArgument
import to.rxs.kommunity.util.httpClient
import to.rxs.kommunity.util.withPermission

@ModuleName("Admin")
@AutoWired
fun newCommand() = module("admin") {
    command("new-welcomechannel") {
        withPermission(Permission.Administrator)

        dev.kord.x.commands.kord.module.command("test") {
            invoke {
                respond("yo")
            }
        }

        invoke(
            TextChannelArgument,
            QuotedArgument(),
            URLArgument,
            QuotedArgument(),
            HastebinArgument
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
}
