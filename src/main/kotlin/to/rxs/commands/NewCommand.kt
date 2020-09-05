@file:AutoWired

package to.rxs.commands

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.kord.argument.TextChannelArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import io.ktor.client.request.get
import to.rxs.commands.arguments.HastebinArgument
import to.rxs.commands.arguments.SentenceArgument
import to.rxs.commands.arguments.URLArgument
import to.rxs.util.httpClient
import to.rxs.util.withPermission

fun newCommand() = module("admin") {
    command("new-welcomechannel") {
        withPermission(Permission.Administrator)

        invoke(TextChannelArgument, SentenceArgument(), URLArgument, SentenceArgument(),  HastebinArgument) { channel, titleSentence, avatarUrl, footerSentence, hastebinMatch ->
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
