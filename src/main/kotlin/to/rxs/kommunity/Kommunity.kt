package to.rxs.kommunity

import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.x.commands.kord.bot
import dev.kord.x.commands.kord.model.prefix.kord
import dev.kord.x.commands.kord.model.prefix.mention
import dev.kord.x.commands.model.prefix.literal
import dev.kord.x.commands.model.prefix.or
import kapt.kotlin.generated.configure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import to.rxs.kommunity.core.GameAnimator
import to.rxs.kommunity.io.connect
import to.rxs.kommunity.listeners.joinRolesListener
import to.rxs.kommunity.listeners.selfMentionListener
import to.rxs.kommunity.youtube.CallbackServer
import to.rxs.kommunity.youtube.YoutubeEventListener

private val log = KotlinLogging.logger {}

class Kommunity {

    private lateinit var kord: Kord
    private lateinit var gameAnimator: GameAnimator

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown))
    }

    suspend fun launch() {
        log.info { "Connecting to database..." }
        connect()

        kord = Kord(Config.DISCORD_TOKEN)

        initialize()
    }

    private suspend fun initialize() {
        bot(kord) {
            configure()

            prefix {
                kord { literal(Config.PREFIX) or mention() }
            }

            kord.apply {
                selfMentionListener()
                joinRolesListener()
                on<ReadyEvent> {
                    start()
                }
            }
        }
    }

    private suspend fun start() {
        GlobalScope.launch {
            log.info { "Starting game animator..." }
            gameAnimator = GameAnimator(kord)
            gameAnimator.start()
        }

        GlobalScope.launch {
            log.info { "Initializing YouTube event listener..." }
            CallbackServer.registerListener(YoutubeEventListener(kord))
            CallbackServer.start()
        }
    }

    private fun shutdown() {
        log.info { "Shutting down application..." }
        if (::gameAnimator.isInitialized) {
            gameAnimator.close()
        }
    }
}
