package to.rxs.commands

import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.annotation.ModuleName
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.model.respondEmbed
import com.gitlab.kordlib.kordx.commands.kord.module.command
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import java.time.Duration
import java.time.Instant
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@AutoWired
@ModuleName("owner")
fun evalCommand() = command("eval") {
    precondition {
        if (author.id.longValue == 254892085000405004L) { // TODO: change to team members once PR was merged
            true
        } else {
            respond("This command can only be executed by an owner of the application!")
            false
        }
    }
    invoke(StringArgument) { code ->
        val message = respondEmbed {
            title = "<a:loading:739511688243577003> Compiling..."
            description = "```Loading...```"
        }

        val engine = ScriptEngineManager().getEngineByName("kotlin")

        val compilationTimeStart = Instant.now()

        val response = try {
            engine.put("dis", this)
            engine.eval(code)
        } catch (e: ScriptException) {
            e.message
        }

        val compilationTime = Duration.between(compilationTimeStart, Instant.now()).toMillis() / 1000.0

        message.edit {
            embed {
                title = "⏱ Compiled in ${"%.2f".format(compilationTime)}s"
                description = "```$response```"
            }
        }
    }
}
