package to.rxs.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RoleChannels : IntIdTable("role_channels") {
    val type = enumeration("type", ChannelType::class)
    val channelId = long("channel_id")
    val reactionEmojiId = long("reaction_emoji_id")
    val reactionRoleId = long("reaction_role_id")
    val guildId = long("guild_id")
}

class RoleChannel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoleChannel>(RoleChannels)
    var type by RoleChannels.type
    var channelId by RoleChannels.channelId
    var reactionEmojiId by RoleChannels.reactionEmojiId
    var reactionRoleId by RoleChannels.reactionRoleId
    var guildId by RoleChannels.guildId
}

enum class ChannelType {
    CATEGORY,
    CHANNEL
}
