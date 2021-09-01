package to.rxs.kommunity.command.slashcommands

import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.bulkEditSlashCommandPermissions
import dev.kord.core.behavior.createApplicationCommands
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.processor.CommandProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import to.rxs.kommunity.Config
import to.rxs.kommunity.Environment
import to.rxs.kommunity.entities.Permission
import to.rxs.kommunity.util.permission

suspend fun CommandProcessor.updateSlashCommands(kord: Kord) {
    val commandConfigure:
        MultiApplicationCommandBuilder.() -> Unit = {
        this@updateSlashCommands.commands.values.distinct().forEach { addCommand(it) }
    }

    val guild = kord.getGuild(Config.GUILD_ID) ?: error("Could not find dev guild")

    val registeredCommands = when (Config.ENVIRONMENT) {
        Environment.DEVELOPMENT -> {
            guild.createApplicationCommands(commandConfigure)
        }
        Environment.PRODUCTION -> kord.createGlobalApplicationCommands(commandConfigure)
    }

    registeredCommands.registerPermissions(this.commands, guild)
}

private fun MultiApplicationCommandBuilder.addCommand(command: Command<*>) {
    input(command.name, command.description) {
        defaultPermission = command.permission == Permission.EVERYONE

        command.arguments.forEach {
            val argument = it as? SlashArgument<*, *>
                ?: error("Argument $it in ${command.name} is not a slash argument")
            with(argument) {
                this@input.applyArgument()
            }
        }
    }
}

private suspend fun Flow<ApplicationCommand>.registerPermissions(
    commands: Map<String, Command<*>>,
    guild: GuildBehavior
) {
    val list = toList()
    guild.bulkEditSlashCommandPermissions {
        list.forEach {
            val command = commands[it.name] ?: error("Could not register permissions for: ${it.name}")

            val permission = command.permission
            if (permission != Permission.EVERYONE) {
                command(it.id) {
                    role(permission.roleId)
                }
            }
        }
    }
}
