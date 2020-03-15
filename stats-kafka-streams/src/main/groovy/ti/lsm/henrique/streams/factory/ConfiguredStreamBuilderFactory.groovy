package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig

import javax.inject.Singleton

@Factory
@CompileStatic
class ConfiguredStreamBuilderFactory {

    @Singleton
    ConfiguredStreamBuilder configuredStreamBuilder(final ConfiguredStreamBuilder builder) {
        final Properties props = builder.getConfiguration()
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().getClass().getName()
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().getClass().getName()
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return builder
    }
}
