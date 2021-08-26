package to.rxs.kommunity.core

import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import to.rxs.kommunity.Config
import java.util.concurrent.TimeUnit

class GameAnimator(private val client: Kord) {

    @OptIn(ObsoleteCoroutinesApi::class)
    private val ticker = ticker(TimeUnit.MINUTES.toMillis(5), 0, client.coroutineContext)
    private val games = Config.GAMES

    suspend fun start() {
        for (unit in ticker) {
            client.editPresence {
                status = PresenceStatus.Online
                playing(games.random())
            }
        }
    }

    fun close() = ticker.cancel()
}
