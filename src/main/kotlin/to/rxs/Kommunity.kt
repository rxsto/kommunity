package to.rxs

import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.on
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.mention
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.or
import com.gitlab.kordlib.kordx.commands.model.prefix.prefix
import kapt.kotlin.generated.configure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import to.rxs.core.GameAnimator
import to.rxs.io.connect

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
                    kord { literal("!") or mention() }
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
            log.info { "Starting game animator ...." }
            gameAnimator.start()
        }
    }

    private fun shutdown() {
        log.info { "Shutting down application..." }
        if (::gameAnimator.isInitialized) {
            gameAnimator.close()
        }
    }
}
