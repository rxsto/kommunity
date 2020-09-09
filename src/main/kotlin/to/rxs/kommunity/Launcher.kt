package to.rxs.kommunity

import org.apache.logging.log4j.core.config.Configurator

suspend fun main() {
    Configurator.setRootLevel(Config.LOG_LEVEL)

    Kommunity().start()
}
