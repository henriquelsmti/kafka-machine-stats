package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ti.lsm.henrique.model.*
import ti.lsm.henrique.streams.consumeds.*
import ti.lsm.henrique.streams.model.ProcessResourcesUsage
import ti.lsm.henrique.streams.produced.ProcessResourcesUsageProduced

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import java.time.Duration

@Factory
@CompileStatic
class ProcessResourcesUsageStream {

    public static final String PROCESS_RESOURCES_USAGE = 'process-resources-usage'

    private static Logger LOG = LoggerFactory.getLogger(ProcessResourcesUsageStream)

    @Inject
    @Named(ProcessStatsRecord.topic)
    KStream<String, ProcessStatsRecord> processStatsRecordStream

    @Inject
    @Named(ProcessIoStatsRecord.topic)
    KStream<String, ProcessIoStatsRecord> processIoStatsRecordStream

    @Inject
    ComputerRecordConsumed computerRecordConsumed

    @Inject
    @Named(ComputerRecord.topic)
    GlobalKTable<String, ComputerRecord> computerRecordTable

    @Inject
    ConfiguredStreamBuilder builder

    @Inject
    ProcessResourcesUsageProduced processResourcesUsageProduced


    @Bean
    @Inject
    @Singleton
    @Named('process-resources-usage')
    KStream<String, ProcessResourcesUsage> processResourcesUsageStream(JacksonObjectSerializer objectSerializer) {

        final JoinWindows windows = JoinWindows.of(Duration.ofMinutes(1))

        KStream<String, ProcessResourcesUsage> stream = processStatsRecordStream
                .join(processIoStatsRecordStream, (processStatsRecord, processIoStatsRecord) -> {
                    return new ProcessResourcesUsage(
                            processStatsRecord: processStatsRecord,
                            processIoStatsRecord: processIoStatsRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<ProcessStatsRecord>(objectSerializer, ProcessStatsRecord), new JsonSerde<ProcessIoStatsRecord>(objectSerializer, ProcessIoStatsRecord)))
                .join(computerRecordTable,
                        (String key, ProcessResourcesUsage processResourcesUsage) -> {
                            return processResourcesUsage.processStatsRecord.computerIdentifier
                        },
                        (processResourcesUsage, computerRecord) -> {
                            return new ProcessResourcesUsage(
                                    processStatsRecord: processResourcesUsage.processStatsRecord,
                                    processIoStatsRecord: processResourcesUsage.processIoStatsRecord,
                                    computerRecord: computerRecord
                            )
                        }
                )

        stream.to(PROCESS_RESOURCES_USAGE, processResourcesUsageProduced)
        LOG.debug("Bean ${PROCESS_RESOURCES_USAGE} created!")
        return stream
    }
}
