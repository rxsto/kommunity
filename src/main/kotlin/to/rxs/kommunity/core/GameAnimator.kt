package to.rxs.kommunity.core

import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ticker
import to.rxs.kommunity.Config
import to.rxs.kommunity.util.NamedThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class GameAnimator(private val client: Kord) {

    private val context = Executors.newSingleThreadExecutor(NamedThreadFactory("game-animator")).asCoroutineDispatcher()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val ticker = ticker(TimeUnit.MINUTES.toMillis(5), 0, context)
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
