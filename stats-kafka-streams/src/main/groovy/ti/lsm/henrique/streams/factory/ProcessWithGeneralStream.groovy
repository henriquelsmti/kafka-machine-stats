package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.KStream
import ti.lsm.henrique.streams.model.GeneralMachineStats
import ti.lsm.henrique.streams.model.ProcessResourcesUsage
import ti.lsm.henrique.streams.model.ProcessWithGeneral

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import java.time.Duration

@Factory
@CompileStatic
class ProcessWithGeneralStream {

    public static final String PROCESS_WITH_GENERAL = "process-with-general"

    @Inject
    @Named(GeneralMachineStatsStream.GENERAL_MACHINE_STATS)
    KStream<String, GeneralMachineStats> generalMachineStatsStream

    @Inject
    @Named(ProcessResourcesUsageStream.PROCESS_RESOURCES_USAGE)
    KStream<String, ProcessResourcesUsage> processResourcesUsageStream

    @Inject
    ConfiguredStreamBuilder builder

    @Bean
    @Singleton
    @Named(PROCESS_WITH_GENERAL)
    KStream<String, ProcessWithGeneral> processWithGeneral() {

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
                        windows)
                .selectKey((key, ProcessWithGeneral processWithGeneral) -> {
                    return processWithGeneral.processResourcesUsage.processStatsRecord.key
                })
                .through(PROCESS_WITH_GENERAL)

    }
}
