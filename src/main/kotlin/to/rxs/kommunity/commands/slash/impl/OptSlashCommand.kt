package to.rxs.kommunity.commands.slash.impl

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.Interaction
import dev.kord.x.emoji.Emojis
import to.rxs.kommunity.Config
import to.rxs.kommunity.commands.slash.SlashCommand
import to.rxs.kommunity.misc.EmojiHelper

@KordPreview
object OptSlashCommand : SlashCommand {

    override suspend fun handle(kord: Kord, interaction: Interaction) {
        val action = interaction.command.options["action"]!!.value
        val guild = kord.getGuild(interaction.data.guildId.value!!)!!
        val role = Config.ROLES[interaction.command.options["notification"]!!.value]!!

        if (action == "in") {
            kord.rest.guild.addRoleToGuildMember(guild.id, interaction.user.id, role, "User opt in command")
        } else if (action == "out") {
            kord.rest.guild.deleteRoleFromGuildMember(guild.id, interaction.user.id, role, "User opt out command")
        }

        interaction.respondPublic {
            embed {
                title = "${EmojiHelper.SUCCESS} Success!"
                description = "Successfully opted ${
                    when (action) {
                        "in" -> "**in** to"
                        "out" -> "**out** from"
                        else -> "undefined"
                    }
                } `${interaction.command.options["notification"]!!.value}` notifications!"
            }
        }
    }
}
