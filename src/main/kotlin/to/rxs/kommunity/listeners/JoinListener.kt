package to.rxs.kommunity.listeners

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.guild.MemberJoinEvent
import com.gitlab.kordlib.core.on
import to.rxs.kommunity.Config

fun Kord.registerJoinRolesListener() = on<MemberJoinEvent> {
    member.addRole(Snowflake(Config.SERVER_NEWS_ROLE))
    member.addRole(Snowflake(Config.VIDEO_NEWS_ROLE))
    member.addRole(Snowflake(Config.STREAM_NEWS_ROLE))
}
