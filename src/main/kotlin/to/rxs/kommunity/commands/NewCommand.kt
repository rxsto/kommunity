@file:AutoWired

package to.rxs.kommunity.commands

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.QuotedArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.TextChannelArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import io.ktor.client.request.get
import to.rxs.kommunity.commands.arguments.HastebinArgument
import to.rxs.kommunity.commands.arguments.URLArgument
import to.rxs.kommunity.util.httpClient
import to.rxs.kommunity.util.withPermission

fun newCommand() = module("admin") {
    command("new-welcomechannel") {
        withPermission(Permission.Administrator)

        com.gitlab.kordlib.kordx.commands.kord.module.command("test") {
            invoke {
                respond("yo")
            }
        }

        invoke(TextChannelArgument, QuotedArgument(), URLArgument, QuotedArgument(),  HastebinArgument) { channel, titleSentence, avatarUrl, footerSentence, hastebinMatch ->
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
