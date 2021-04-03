package to.rxs.kommunity.commands

import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.Argument
import dev.kord.x.commands.argument.result.ArgumentResult
import dev.kord.x.commands.argument.result.extension.FilterResult
import dev.kord.x.commands.argument.result.extension.filter
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.argument.text.WordArgument
import dev.kord.x.commands.kord.model.respondEmbed
import dev.kord.x.commands.kord.module.command
import dev.kord.x.commands.model.command.invoke
import kotlinx.coroutines.flow.firstOrNull
import to.rxs.kommunity.Config

@AutoWired
@ModuleName("general")
fun optOutCommand() = command("opt") {
    invoke(WordArgument.whitelist("in", "out"), StringArgument) { operation, argument ->
        val optOut = operation.equals("out", ignoreCase = true)

        val guild = guild ?: return@invoke
        val role = guild.roles.firstOrNull { it.name.equals(argument, ignoreCase = true) }

        if (role == null || role.id.value !in Config.OPT_OUT_ROLES) {
            respondEmbed {
                title = "Role not found"
                description = "Possible roles to opt out are `Server News`, `Video News` and `Stream News`."
            }
            return@invoke
        }

        if (optOut) {
            kord.rest.guild.deleteRoleFromGuildMember(guild.id, author.id, role.id, "User opt out command")
            respondEmbed {
                title = "Opted out"
                description = "Successfully opted out of `${role.name}` notifications"
            }
        } else {
            kord.rest.guild.addRoleToGuildMember(guild.id, author.id, role.id, "User opt in command")
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
    vararg whitelist: String, ignoreCase: Boolean = true
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
