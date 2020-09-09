package to.rxs.kommunity.io

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import to.rxs.kommunity.Config
import to.rxs.kommunity.Environment
import java.nio.file.Files
import java.nio.file.Path

fun connect() {
    if (Config.ENVIRONMENT == Environment.DEVELOPMENT) {
        val path = Path.of("db.sqlite")
        if (!Files.exists(path)) {
            Files.createFile(path)
        }
        Database.connect("jdbc:sqlite:${path.toAbsolutePath()}")
    } else {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql:${Config.POSTGRES_URL}"
            username = Config.POSTGRES_USERNAME
            password = Config.POSTGRES_PASSWORD
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
}
