package to.rxs.kommunity.util

import dev.kord.core.Kord
import dev.kord.x.commands.kord.BotBuilder
import dev.kord.x.commands.kord.model.processor.KordProcessorBuilder
import dev.kord.x.commands.model.processor.CommandProcessor

@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") // compiler is stupid here
suspend inline fun bot(kord: Kord, configure: suspend KordProcessorBuilder.() -> Unit, callback: CommandProcessor.() -> Unit) {
    val builder = BotBuilder(kord)
    builder.processorBuilder.configure()
    builder.build().callback()
    kord.login()
}
