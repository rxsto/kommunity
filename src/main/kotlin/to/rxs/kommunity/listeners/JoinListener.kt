package to.rxs.kommunity.listeners

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.on
import dev.kord.x.emoji.Emojis
import mu.KotlinLogging
import to.rxs.kommunity.Config

private val log = KotlinLogging.logger {}

fun Kord.joinListener() = on<MemberJoinEvent> {
    if (guildId.asString == Config.GUILD_ID && !member.isBot) {
        log.debug { "Adding roles to ${member.displayName} (${member.id})..." }
        member.addRole(Snowflake(Config.SERVER_NEWS_ROLE))
        member.addRole(Snowflake(Config.VIDEO_NEWS_ROLE))
        member.addRole(Snowflake(Config.STREAM_NEWS_ROLE))

        log.debug { "Sending welcome message..." }
        rest.channel.createMessage(Snowflake(Config.WELCOME_CHANNEL)) {
            content =
                "${Emojis.wave} hey <@${member.id.asString}>! welcome to rxsto's kommunity! ${Emojis.blush} enjoy your time being! ${Emojis.wink}"
        }
    }
}
