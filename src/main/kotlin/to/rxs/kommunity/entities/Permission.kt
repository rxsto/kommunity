package to.rxs.kommunity.entities

import dev.kord.common.entity.Snowflake
import to.rxs.kommunity.Config

interface PermissionProvider {
    val roleId: Snowflake
}

enum class Permission : PermissionProvider {
    EVERYONE {
        override val roleId: Snowflake
            get() = throw UnsupportedOperationException("Everyone doesn't have a role")
    },
    ADMIN {
        override val roleId: Snowflake
            get() = Config.ADMIN_ROLE
    }
}
