package ti.lsm.henrique

import io.micronaut.configuration.kafka.config.AbstractKafkaConfiguration


object TestConfigs {
    var config: Map<String, Any> = mapOf(
            AbstractKafkaConfiguration.EMBEDDED to true
    )
}