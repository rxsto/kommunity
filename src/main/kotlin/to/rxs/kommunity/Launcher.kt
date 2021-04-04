package to.rxs.kommunity

import org.apache.logging.log4j.core.config.Configurator

suspend fun main() {
    // TODO: switch logger
    Configurator.setRootLevel(Config.LOG_LEVEL)

    Kommunity().launch()
}
