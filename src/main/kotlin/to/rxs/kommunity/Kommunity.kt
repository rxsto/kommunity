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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import to.rxs.kommunity.core.GameAnimator
import to.rxs.kommunity.io.connect
import to.rxs.kommunity.listeners.registerJoinRolesListener
import to.rxs.kommunity.youtube.CallbackServer
import to.rxs.kommunity.youtube.YoutubeEventListener

private val log = KotlinLogging.logger {}

class Kommunity {

    private lateinit var client: Kord
    private lateinit var gameAnimator: GameAnimator

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown))
    }

    suspend fun start() {
        client = Kord(Config.DISCORD_TOKEN)

        GlobalScope.launch(Dispatchers.IO) {
            bot(client) {
                prefix {
                    kord { literal(Config.PREFIX) or mention() }
                }
                build()
            }

            client.login {
                log.info { "Logging in..." }
                status = PresenceStatus.DoNotDisturb
                playing("Starting...")
            }
        }

        gameAnimator = GameAnimator(client)

        log.info { "Connecting to database..." }
        connect()

        client.on<ReadyEvent> {
            log.info { "Starting game animator..." }
            gameAnimator.start()
        }

        log.info { "Initializing member join listener..." }
        client.registerJoinRolesListener()

        CallbackServer.registerListener(YoutubeEventListener(client))
        CallbackServer.start()
    }

    private fun shutdown() {
        log.info { "Shutting down application..." }
        if (::gameAnimator.isInitialized) {
            gameAnimator.close()
        }
    }
}
