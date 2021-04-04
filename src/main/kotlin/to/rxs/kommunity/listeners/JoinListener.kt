package to.rxs.kommunity.listeners

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.on
import to.rxs.kommunity.Config

fun Kord.joinRolesListener() = on<MemberJoinEvent> {
    member.addRole(Snowflake(Config.SERVER_NEWS_ROLE))
    member.addRole(Snowflake(Config.VIDEO_NEWS_ROLE))
    member.addRole(Snowflake(Config.STREAM_NEWS_ROLE))
}
