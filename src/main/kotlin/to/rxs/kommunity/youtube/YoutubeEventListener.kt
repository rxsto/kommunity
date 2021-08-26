package to.rxs.kommunity.youtube

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.request.*
import kotlinx.datetime.Instant
import to.rxs.kommunity.Config
import to.rxs.kommunity.util.httpClient

class YoutubeEventListener(private val client: Kord) : YouTubeEventSubscriber {

    override suspend fun onEvent(event: YouTubeEvent) {
        val thumbnailUrl = "https://img.youtube.com/vi/${event.entry.videoId}/hqdefault.jpg"
        val colorCode = httpClient.get<String>("https://color.aero.bot/dominant?image=$thumbnailUrl")
        client.rest.channel.createMessage(Snowflake(Config.VIDEOS_CHANNEL)) {
            content = "<@&${Config.VIDEO_NEWS_ROLE}> \uD83D\uDD14 ${event.entry.link.href}"
            embed {
                title = event.entry.title
                url = event.entry.link.href
                author {
                    icon = "https://rxs.to/icon.png"
                    name = event.entry.author.name
                    url = event.entry.author.uri
                }
                image = thumbnailUrl
                color = dev.kord.common.Color(colorCode.toInt())
                timestamp = Instant.parse(event.entry.published)
            }
        }
    }
}
