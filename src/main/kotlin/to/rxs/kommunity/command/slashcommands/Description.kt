package to.rxs.kommunity.command.slashcommands

import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.command.CommandBuilder
import dev.kord.x.commands.model.metadata.Metadata

@PublishedApi
internal object Description : Metadata.Key<String>

fun CommandBuilder<*, *, *>.description(description: String) {
    metaData[Description] = description
}

val Command<*>.description: String
    get() = data.metadata[Description] ?: error("Command $name, does not have a description")
