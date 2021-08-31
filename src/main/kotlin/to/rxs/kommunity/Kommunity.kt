package to.rxs.kommunity

import dev.kord.core.Kord
import dev.kord.core.behavior.createApplicationCommands
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.x.commands.kord.model.prefix.kord
import dev.kord.x.commands.kord.model.prefix.mention
import dev.kord.x.commands.kord.model.processor.KordProcessorBuilder
import dev.kord.x.commands.model.prefix.literal
import dev.kord.x.commands.model.prefix.or
import kapt.kotlin.generated.configure
import kotlinx.coroutines.launch
import mu.KotlinLogging
import to.rxs.kommunity.command.slashcommands.addCommand
import to.rxs.kommunity.core.GameAnimator
import to.rxs.kommunity.io.connect
import to.rxs.kommunity.listeners.joinListener
import to.rxs.kommunity.listeners.selfMentionListener
import to.rxs.kommunity.util.bot
import to.rxs.kommunity.youtube.CallbackServer
import to.rxs.kommunity.youtube.YoutubeEventListener

private val log = KotlinLogging.logger {}

class Kommunity {

    private lateinit var kord: Kord
    private lateinit var gameAnimator: GameAnimator

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown))
    }

    @OptIn(PrivilegedIntent::class)
    suspend fun launch() {
        log.info { "Connecting to database..." }
        connect()

        kord = Kord(Config.DISCORD_TOKEN) {
            intents = Intents.all
        }

        initialize()
    }

    private suspend fun initialize() {
        val configureBot: suspend KordProcessorBuilder.() -> Unit = {
            configure()

            prefix {
                kord { literal(Config.PREFIX) or mention() }
            }

            kord.apply {
                selfMentionListener()
                joinListener()
                on<ReadyEvent> {
                    start()
                }
            }
        }

        bot(kord, configureBot) {
            val commands = commands.values.distinct()

            val commandConfigure:
                    MultiApplicationCommandBuilder.() -> Unit = {
                commands.forEach { addCommand(it) }
            }

            when (Config.ENVIRONMENT) {
                Environment.DEVELOPMENT -> {
                    val guild = kord.getGuild(Config.GUILD_ID) ?: error("Could not find dev guild")

                    guild.createApplicationCommands(commandConfigure)
                }
                Environment.PRODUCTION -> kord.createGlobalApplicationCommands(commandConfigure)
            }
        }
    }

    private suspend fun start() {
        kord.launch {
            log.info { "Starting game animator..." }
            gameAnimator = GameAnimator(kord)
            gameAnimator.start()
        }

        kord.launch {
            log.info { "Initializing YouTube event listener..." }
            CallbackServer.registerListener(YoutubeEventListener(kord))
            CallbackServer.start(kord)
        }
    }

    private fun shutdown() {
        log.info { "Shutting down application..." }
        if (::gameAnimator.isInitialized) {
            gameAnimator.close()
        }
    }
}
