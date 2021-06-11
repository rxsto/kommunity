package to.rxs.kommunity.commands

import dev.kord.core.behavior.edit
import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.kord.model.respondEmbed
import dev.kord.x.commands.kord.module.command
import dev.kord.x.commands.model.command.invoke
import mu.KotlinLogging
import java.time.Duration
import java.time.Instant
import javax.script.ScriptEngineManager
import javax.script.ScriptException

private val log = KotlinLogging.logger {}

@AutoWired
@ModuleName("owner")
fun evalCommand() = command("eval") {
    precondition {
        if (kord.rest.application.getCurrentApplicationInfo().team?.members?.any { it.user.id == author.id }!!
                .or(false)
        ) {
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
