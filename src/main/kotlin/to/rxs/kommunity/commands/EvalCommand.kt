package to.rxs.kommunity.commands

import dev.kord.rest.builder.message.create.embed
import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.annotation.ModuleName
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.model.command.invoke
import to.rxs.kommunity.command.slashcommands.command
import to.rxs.kommunity.command.slashcommands.description
import to.rxs.kommunity.command.slashcommands.respond.editEmbed
import to.rxs.kommunity.command.slashcommands.respond.respondEmbed
import java.time.Duration
import java.time.Instant
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@AutoWired
@ModuleName("owner")
fun evalCommand() = command("eval") {
    description("Allows the bot owners to evaluate code")

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

    invoke {
        respondEmbed {
            title = "Please enter code!"
            description = "Please enter the code to execute"
        }

        val code = read(StringArgument)

        val message = followUp {
            embed {
                title = "<a:loading:739511688243577003> Compiling..."
                description = "```Loading...```"
            }
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
        message.editEmbed {
            title = "⏱ Compiled in ${"%.2f".format(compilationTime)}s"
            description = "```$response```"
        }
    }
}
