package to.rxs.kommunity.util

import dev.kord.core.any
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.command.CommandBuilder
import dev.kord.x.commands.model.metadata.Metadata
import dev.kord.x.emoji.Emojis
import to.rxs.kommunity.command.slashcommands.SlashCommandEvent
import to.rxs.kommunity.command.slashcommands.respond.respondEmbed
import to.rxs.kommunity.entities.Permission as PermissionEnum

private object Permission : Metadata.Key<PermissionEnum>

val Command<*>.permission: PermissionEnum
    get() = data.metadata[Permission] ?: PermissionEnum.EVERYONE

fun <T : InteractionResponseBehavior> CommandBuilder<*, *, SlashCommandEvent<T>>.withPermission(permission: PermissionEnum) {
    metaData[Permission] = permission

    precondition {
        if (member.roles.any { it.id == permission.roleId }) {
            true
        } else {
            respondEmbed {
                title = "${Emojis.noEntrySign} You're not permitted to execute this command!"
                description =
                    "In order to successfully execute this command you need to have the `$permission` permission."
            }
            false
        }
    }
}
