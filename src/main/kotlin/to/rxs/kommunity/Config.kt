package to.rxs.kommunity

import dev.kord.common.entity.Snowflake
import org.apache.logging.log4j.Level
import dev.schlaubi.envconf.Config as EnvironmentConfig

object Config : EnvironmentConfig("") {

    val GAMES by getEnv { it.split("|") }
    val PREFIX by environment
    val DATABASE_ACTIVE by environment
    val POSTGRES_URL by environment
    val POSTGRES_USERNAME by environment
    val POSTGRES_PASSWORD by environment
    val ENVIRONMENT by getEnv { Environment.valueOf(it) }
    val LOG_LEVEL by getEnv { Level.valueOf(it) }
    val DISCORD_TOKEN by environment
    val NOTIFICATION_SERVER_PORT by getEnv(1337) { it.toInt() }
    val GUILD_ID by getEnv { Snowflake(it) }
    val ROLES by getEnv { createRolesMap(it) }
    val SERVER_NEWS_ROLE by environment
    val ADMIN_ROLE by getEnv { Snowflake(it) }
    val VIDEO_NEWS_ROLE by environment
    val STREAM_NEWS_ROLE by environment
    val OPT_OUT_ROLES = listOf(SERVER_NEWS_ROLE, VIDEO_NEWS_ROLE, STREAM_NEWS_ROLE)
    val NEWS_CHANNEL by environment
    val VIDEOS_CHANNEL by environment
    val STREAMS_CHANNEL by environment
    val WELCOME_CHANNEL by environment
}

enum class Environment {
    DEVELOPMENT,
    PRODUCTION
}

@OptIn(ExperimentalStdlibApi::class)
fun createRolesMap(env: String): Map<String, Snowflake> {
    return env.split(",")
        .associateBy {
            it.substringBefore(':')
        }
        .mapValues { Snowflake(it.value.substringBefore(':')) }
}
