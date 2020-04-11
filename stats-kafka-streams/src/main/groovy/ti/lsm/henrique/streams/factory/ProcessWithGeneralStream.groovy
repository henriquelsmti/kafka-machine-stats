package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import ti.lsm.henrique.streams.model.GeneralMachineStats
import ti.lsm.henrique.streams.model.ProcessResourcesUsage
import ti.lsm.henrique.streams.model.ProcessWithGeneral
import ti.lsm.henrique.streams.produced.ProcessWithGeneralProduced

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import java.time.Duration

@Factory
@CompileStatic
class ProcessWithGeneralStream {

    public static final String PROCESS_WITH_GENERAL = 'process-with-general'

    @Inject
    @Named('general-machine-stats')
    KStream<String, GeneralMachineStats> generalMachineStatsStream

    @Inject
    @Named('process-resources-usage')
    KStream<String, ProcessResourcesUsage> processResourcesUsageStream

    @Inject
    ConfiguredStreamBuilder builder

    @Inject
    ProcessWithGeneralProduced processWithGeneralProduced

    @Bean
    @Inject
    @Singleton
    @Named('process-with-general')
    KStream<String, ProcessWithGeneral> processWithGeneral(JacksonObjectSerializer objectSerializer) {

        final JoinWindows windows = JoinWindows.of(Duration.ofMinutes(1))

        return processResourcesUsageStream
                .selectKey((key, ProcessResourcesUsage processResourcesUsage) -> {
                    return processResourcesUsage.computerRecord.key
                })
                .join(generalMachineStatsStream,
                        (processResourcesUsage, generalMachineStats) -> {
                            return new ProcessWithGeneral(
                                    generalMachineStats: generalMachineStats,
                                    processResourcesUsage: processResourcesUsage
                            )
                        },
                        windows, Joined.with(Serdes.String(), new JsonSerde<ProcessResourcesUsage>(objectSerializer, ProcessResourcesUsage), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats)))
                .selectKey((key, ProcessWithGeneral processWithGeneral) -> {
                    return processWithGeneral.processResourcesUsage.processStatsRecord.key
                })
                .through(PROCESS_WITH_GENERAL, processWithGeneralProduced)

    }
}
