package to.rxs.kommunity.commands

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.annotation.ModuleName
import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.result.ArgumentResult
import com.gitlab.kordlib.kordx.commands.argument.result.extension.FilterResult
import com.gitlab.kordlib.kordx.commands.argument.result.extension.filter
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.argument.text.WordArgument
import com.gitlab.kordlib.kordx.commands.kord.model.respondEmbed
import com.gitlab.kordlib.kordx.commands.kord.module.command
import com.gitlab.kordlib.kordx.commands.model.command.invoke
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
            kord.rest.guild.deleteRoleFromGuildMember(guild.id.value, author.id.value, role.id.value, "User opt out command")
            respondEmbed {
                title = "Opted out"
                description = "Successfully opted out of `${role.name}` notifications"
            }
        } else {
            kord.rest.guild.addRoleToGuildMember(guild.id.value, author.id.value, role.id.value, "User opt in command")
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
        return this@whitelist.parse(text, fromIndex, context).filter {
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
