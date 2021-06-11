package to.rxs.kommunity

import dev.kord.common.entity.Snowflake
import org.apache.logging.log4j.Level
import to.rxs.kommunity.util.EnvironmentConfig

object Config : EnvironmentConfig("") {

    val GAMES by getEnv { it.split("|") }
    val PREFIX by getEnv()
    val DATABASE_ACTIVE by getEnv()
    val POSTGRES_URL by getEnv()
    val POSTGRES_USERNAME by getEnv()
    val POSTGRES_PASSWORD by getEnv()
    val ENVIRONMENT by getEnv { Environment.valueOf(it) }
    val LOG_LEVEL by getEnv { Level.valueOf(it) }
    val DISCORD_TOKEN by getEnv()
    val NOTIFICATION_SERVER_PORT by getEnv(1337) { it.toInt() }
    val GUILD_ID by getEnv()
    val ROLES by getEnv { createRolesMap(it) }
    val SERVER_NEWS_ROLE by getEnv()
    val VIDEO_NEWS_ROLE by getEnv()
    val STREAM_NEWS_ROLE by getEnv()
    val OPT_OUT_ROLES = listOf(SERVER_NEWS_ROLE, VIDEO_NEWS_ROLE, STREAM_NEWS_ROLE)
    val NEWS_CHANNEL by getEnv()
    val VIDEOS_CHANNEL by getEnv()
    val STREAMS_CHANNEL by getEnv()
    val WELCOME_CHANNEL by getEnv()

}

enum class Environment {
    DEVELOPMENT,
    PRODUCTION
}

fun createRolesMap  (env: String) : Map<String, Snowflake> {
    val roles = HashMap<String, Snowflake>()
    val parts = env.split(",")
    for (part: String in parts) {
        val separated = part.split(":")
        roles[separated[0]] = Snowflake(separated[1])
    }
    return roles
}
