package to.rxs

import io.github.cdimascio.dotenv.dotenv
import org.apache.logging.log4j.Level

object Config {

    private val dotenv = dotenv()

    val GAMES = dotenv["GAMES"]?.split("|") ?: error("Missing activity games")
    val POSTGRES_URL = dotenv["POSTGRES_URL"] ?: error("Missing PostgreSQL connection URL")
    val POSTGRES_USERNAME = dotenv["POSTGRES_USERNAME"] ?: error("Missing PostgreSQL username")
    val POSTGRES_PASSWORD = dotenv["POSTGRES_PASSWORD"] ?: error("Missing PostgreSQL password")
    val ENVIRONMENT = dotenv["ENVIRONMENT"]?.let { Environment.valueOf(it) } ?: error("Missing environment declaration")
    val LOG_LEVEL = dotenv["LOG_LEVEL"]?.let { Level.valueOf(it) } ?: error("Missing to.rxs.log level declaration")
    val DISCORD_TOKEN = dotenv["DISCORD_TOKEN"] ?: error("Missing Discord token")

}

enum class Environment {
    DEVELOPMENT,
    PRODUCTION
}
