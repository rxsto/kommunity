package to.rxs.kommunity

import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.on
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.mention
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.or
import io.ktor.client.request.*
import kapt.kotlin.generated.configure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import to.rxs.kommunity.core.GameAnimator
import to.rxs.kommunity.io.connect
import to.rxs.kommunity.listeners.registerJoinRolesListener
import to.rxs.kommunity.util.httpClient
import to.rxs.kommunity.youtube.CallbackServer
import to.rxs.kommunity.youtube.YouTubeEventSubscriber
import to.rxs.kommunity.youtube.YoutubeEventListener
import java.awt.Color
import java.time.Instant

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
                configure()
            }

            client.login {
                log.info { "Logging in..." }
                status = Status.DnD
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
