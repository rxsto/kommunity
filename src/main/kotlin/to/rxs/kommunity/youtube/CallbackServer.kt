package to.rxs.kommunity.youtube

import dev.kord.core.Kord
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import to.rxs.kommunity.Config

interface YouTubeEventSubscriber {
    suspend fun onEvent(event: YouTubeEvent)
}

object CallbackServer {

    private val listeners = mutableListOf<YouTubeEventSubscriber>()
    private val youTubeListenerExecutor = Dispatchers.IO + Job()
    private val format = XML {
        Class.forName("kotlinx.serialization.KSerializer")
        autoPolymorphic = true
        repairNamespaces = true
        unknownChildHandler = { _, _, _, _ -> }
    }

    fun registerListener(listener: YouTubeEventSubscriber) {
        listeners.add(listener)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun start(kord: Kord) {
        embeddedServer(Netty, Config.NOTIFICATION_SERVER_PORT) {
            install(ContentNegotiation) {
                serialization(ContentType.Application.Atom, format)
            }

            routing {
                get("youtube/receive") {
                    val message = context.request.queryParameters["hub.challenge"]
                        ?: return@get context.respond(HttpStatusCode.BadRequest, "Missing challenge")
                    context.respond(message)
                }

                post("youtube/receive") {
                    val event: YouTubeEvent = try {
                        context.request.call.receive()
                    } catch (e: SerializationException) {
                        return@post context.respond(HttpStatusCode.UnprocessableEntity)
                    }

                    listeners.forEach {
                        kord.launch(youTubeListenerExecutor) {
                            it.onEvent(event)
                        }
                        context.respond(HttpStatusCode.Accepted)
                    }
                }
            }
        }.start()
    }
}

@Serializable
@XmlSerialName("feed", namespace = "http://www.w3.org/2005/Atom", prefix = "")
data class YouTubeEvent(
    @XmlElement(true) val title: String,
    @XmlElement(true) val updated: String,
    val entry: Entry
) {
    @SerialName("entry")
    @Serializable
    data class Entry(
        @XmlElement(true) val id: String,
        @XmlElement(true) @XmlSerialName(
            "videoId",
            namespace = "https://www.youtube.com/xml/schemas/2015",
            prefix = "yt"
        )
        val videoId: String,
        @XmlElement(true) @XmlSerialName(
            "channelId",
            namespace = "https://www.youtube.com/xml/schemas/2015",
            prefix = "yt"
        )
        val channelId: String,
        @XmlElement(true) val title: String,
        val link: Link,
        val author: Author,
        @XmlElement(true) val published: String,
        @XmlElement(true) val updated: String,
    )

    @Serializable
    @SerialName("author")
    data class Author(
        @XmlElement(true) val name: String,
        @XmlElement(true) val uri: String
    )

    @Serializable
    @SerialName("link")
    data class Link(
        val rel: String,
        val href: String
    )
}
