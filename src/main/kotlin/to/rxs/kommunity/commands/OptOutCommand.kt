package to.rxs.kommunity.commands

import dev.kord.common.entity.Snowflake
import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.Argument
import dev.kord.x.commands.argument.extension.named
import dev.kord.x.commands.argument.result.ArgumentResult
import dev.kord.x.commands.argument.result.extension.FilterResult
import dev.kord.x.commands.argument.result.extension.filter
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.argument.text.WordArgument
import dev.kord.x.commands.model.command.invoke
import to.rxs.kommunity.Config
import to.rxs.kommunity.command.slashcommands.arguments.asSlashArgument
import to.rxs.kommunity.command.slashcommands.description
import to.rxs.kommunity.command.slashcommands.ephemeralCommand
import to.rxs.kommunity.command.slashcommands.respond.respondEmbed

private val OperationArgument = WordArgument.whitelist("in", "out").named("operation").asSlashArgument(
    "The operation to execute"
) {
    choice("Opt-in", "in")
    choice("Opt-out", "out")
}

private val ArgumentRole = StringArgument.named("role")
    .asSlashArgument("The role to opt-in/out") {
        choice("Server News", Config.SERVER_NEWS_ROLE)
        choice("Stream News", Config.STREAM_NEWS_ROLE)
        choice("Video News", Config.VIDEO_NEWS_ROLE)
    }

@AutoWired
@ModuleName("general")
fun optOutCommand() = ephemeralCommand("opt") {
    description("Allows you to opt out- of/in to notification roles")

    invoke(OperationArgument, ArgumentRole) { operation, argument ->
        val optOut = operation.equals("out", ignoreCase = true)

        val guild = guild
        val role = guild.getRole(Snowflake(argument))

        if (role.id.asString !in Config.OPT_OUT_ROLES) {
            respondEmbed {
                title = "Role not found"
                description = "Possible roles to opt out are `Server News`, `Video News` and `Stream News`."
            }
            return@invoke
        }

        if (optOut) {
            member.removeRole(role.id, "User opt out command")
            respondEmbed {
                title = "Opted out"
                description = "Successfully opted out of `${role.name}` notifications"
            }
        } else {
            member.addRole(role.id, "User opt in command")
            respondEmbed {
                title = "Opted in"
                description = "Successfully opted in for `${role.name}` notifications"
            }
        }
    }
}

/**
 * Returns an Argument that, on top of the supplied argument, only accepts values in [whitelist].
 *
 * @param ignoreCase true to ignore character case when comparing strings. By default true.
 */
fun <CONTEXT> Argument<String, CONTEXT>.whitelist(
    vararg whitelist: String,
    ignoreCase: Boolean = true
): Argument<String, CONTEXT> = object : Argument<String, CONTEXT> by this {
    override suspend fun parse(text: String, fromIndex: Int, context: CONTEXT): ArgumentResult<String> {
        // TODO: implement actual functionality
        val failIndex = 0
        return this@whitelist.parse(text, fromIndex, context).filter(failIndex) {
            when {
                ignoreCase -> when {
                    whitelist.any { word -> word.equals(it, true) } -> FilterResult.Pass
                    else -> FilterResult.Fail("expected one of ${whitelist.joinToString(", ")} (not case sensitive) but got $it")
                }
                it in whitelist -> FilterResult.Pass
                else -> FilterResult.Fail("expected one of ${whitelist.joinToString(", ")} (case sensitive) but got $it")
            }
        }
    }
}
