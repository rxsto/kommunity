package to.rxs.util

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.kordx.commands.kord.model.context.KordCommandEvent
import com.gitlab.kordlib.kordx.commands.kord.model.respondEmbed
import com.gitlab.kordlib.kordx.commands.model.command.CommandBuilder

fun CommandBuilder<MessageCreateEvent, MessageCreateEvent, KordCommandEvent>.withPermission(
    permission: Permission
) {
    precondition {
        if (event.member?.getPermissions()?.contains(permission) == true) {
            true
        } else {
            respondEmbed {
                title = "\uD83D\uDEAB You're not permitted to execute this command!"
                description =
                    "In order to successfully execute this command you need to have the `${permission.name}` permission."
            }
            false
        }
    }
}