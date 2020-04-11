package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ti.lsm.henrique.model.*
import ti.lsm.henrique.streams.Application
import ti.lsm.henrique.streams.model.GeneralMachineStats
import ti.lsm.henrique.streams.produced.GeneralMachineStatsProduced

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import java.time.Duration

@Factory
@CompileStatic
class GeneralMachineStatsStream {

    private static Logger LOG = LoggerFactory.getLogger(GeneralMachineStatsStream)

    public static final String GENERAL_MACHINE_STATS = 'general-machine-stats'

    @Inject
    @Named(SystemStatsRecord.topic)
    KStream<String, SystemStatsRecord> systemStatsRecordsStream

    @Inject
    @Named(TasksRecord.topic)
    KStream<String, TasksRecord> tasksRecordStream

    @Inject
    @Named(SwapStatsRecord.topic)
    KStream<String, SwapStatsRecord> swapStatsRecordStream

    @Inject
    @Named(MemoryStatsRecord.topic)
    KStream<String, MemoryStatsRecord> memoryStatsRecordStream

    @Inject
    @Named(DiskStatsRecord.topic)
    KStream<String, DiskStatsRecord> diskStatsRecordStream

    @Inject
    @Named(CpuStatsRecord.topic)
    KStream<String, CpuStatsRecord> cpuStatsRecordStream

    @Inject
    @Named(ComputerRecord.topic)
    GlobalKTable<String, ComputerRecord> computerRecordTable

    @Inject
    ConfiguredStreamBuilder builder

    @Inject
    GeneralMachineStatsProduced generalMachineStatsProduced

    @Bean
    @Inject
    @Singleton
    @Named('general-machine-stats')
    KStream<String, GeneralMachineStats> generalMachineStatsStream(JacksonObjectSerializer objectSerializer) {

        final JoinWindows windows = JoinWindows.of(Duration.ofMinutes(1))

        final KStream<String, GeneralMachineStats> stream = systemStatsRecordsStream
                .join(tasksRecordStream, (systemStatsRecord, tasksRecord) -> {
                    return new GeneralMachineStats(
                            systemStatsRecord: systemStatsRecord,
                            tasksRecord: tasksRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<SystemStatsRecord>(objectSerializer, SystemStatsRecord), new JsonSerde<TasksRecord>(objectSerializer, TasksRecord)))
                .join(swapStatsRecordStream, (generalMachineStats, swapStatsRecord) -> {
                    return new GeneralMachineStats(
                            systemStatsRecord: generalMachineStats.systemStatsRecord,
                            tasksRecord: generalMachineStats.tasksRecord,
                            swapStatsRecord: swapStatsRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats), new JsonSerde<SwapStatsRecord>(objectSerializer, SwapStatsRecord)))
                .join(memoryStatsRecordStream, (generalMachineStats, memoryStatsRecord) -> {
                    return new GeneralMachineStats(
                            systemStatsRecord: generalMachineStats.systemStatsRecord,
                            tasksRecord: generalMachineStats.tasksRecord,
                            swapStatsRecord: generalMachineStats.swapStatsRecord,
                            memoryStatsRecord: memoryStatsRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats), new JsonSerde<MemoryStatsRecord>(objectSerializer, MemoryStatsRecord)))
                .join(diskStatsRecordStream, (generalMachineStats, diskStatsRecord) -> {
                    return new GeneralMachineStats(
                            systemStatsRecord: generalMachineStats.systemStatsRecord,
                            tasksRecord: generalMachineStats.tasksRecord,
                            swapStatsRecord: generalMachineStats.swapStatsRecord,
                            memoryStatsRecord: generalMachineStats.memoryStatsRecord,
                            diskStatsRecord: diskStatsRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats), new JsonSerde<DiskStatsRecord>(objectSerializer, DiskStatsRecord)))
                .join(cpuStatsRecordStream, (generalMachineStats, cpuStatsRecord) -> {
                    return new GeneralMachineStats(
                            systemStatsRecord: generalMachineStats.systemStatsRecord,
                            tasksRecord: generalMachineStats.tasksRecord,
                            swapStatsRecord: generalMachineStats.swapStatsRecord,
                            memoryStatsRecord: generalMachineStats.memoryStatsRecord,
                            diskStatsRecord: generalMachineStats.diskStatsRecord,
                            cpuStatsRecord: cpuStatsRecord
                    )
                }, windows, Joined.with(Serdes.String(), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats), new JsonSerde<CpuStatsRecord>(objectSerializer, CpuStatsRecord)))
                .join(computerRecordTable,
                        (key, computerRecord) -> {
                            KafkaStreams kafkaStreams = Application.context.getBean(KafkaStreams)
                            ReadOnlyKeyValueStore<String, ComputerRecord> keyValueStore =
                                    kafkaStreams.store(ComputerRecord.topic, QueryableStoreTypes.keyValueStore());
                            return key
                        },
                        (generalMachineStats, computerRecord) -> {
                            return new GeneralMachineStats(
                                    systemStatsRecord: generalMachineStats.systemStatsRecord,
                                    tasksRecord: generalMachineStats.tasksRecord,
                                    swapStatsRecord: generalMachineStats.swapStatsRecord,
                                    memoryStatsRecord: generalMachineStats.memoryStatsRecord,
                                    diskStatsRecord: generalMachineStats.diskStatsRecord,
                                    cpuStatsRecord: generalMachineStats.cpuStatsRecord,
                                    computerRecord: computerRecord
                            )
                        })

        stream.to(GENERAL_MACHINE_STATS, generalMachineStatsProduced)
        LOG.debug("Bean ${GENERAL_MACHINE_STATS} created!")
        return stream
    }
}
