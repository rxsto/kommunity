package to.rxs.kommunity.util

import dev.kord.common.entity.Permission
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.x.commands.kord.model.context.KordCommandEvent
import dev.kord.x.commands.kord.model.respondEmbed
import dev.kord.x.commands.model.command.CommandBuilder
import dev.kord.x.emoji.Emojis

fun CommandBuilder<MessageCreateEvent, MessageCreateEvent, KordCommandEvent>.withPermission(
    permission: Permission
) {
    precondition {
        if (event.member?.getPermissions()?.contains(permission) == true) {
            true
        } else {
            respondEmbed {
                title = "${Emojis.noEntrySign} You're not permitted to execute this command!"
                description =
                    "In order to successfully execute this command you need to have the `${permission}` permission."
            }
            false
        }
    }
}
