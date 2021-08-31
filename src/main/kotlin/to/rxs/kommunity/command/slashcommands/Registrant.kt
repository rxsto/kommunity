package to.rxs.kommunity.command.slashcommands

import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.x.commands.model.command.Command

fun MultiApplicationCommandBuilder.addCommand(command: Command<*>) {
    input(command.name, command.description) {
        command.arguments.forEach {
            val argument = it as? SlashArgument<*, *>
                ?: error("Argument $it in ${command.name} is not a slash argument")
            with(argument) {
                this@input.applyArgument()
            }
        }
    }

}
