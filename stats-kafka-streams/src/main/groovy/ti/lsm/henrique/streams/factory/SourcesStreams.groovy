package ti.lsm.henrique.streams.factory

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.kstream.KStream
import ti.lsm.henrique.model.*
import ti.lsm.henrique.streams.consumeds.*

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Factory
@CompileStatic
class SourcesStreams {

    @Inject
    private ComputerRecordConsumed computerRecordConsumed
    @Inject
    private CpuStatsRecordConsumed cpuStatsRecordConsumed
    @Inject
    private DiskStatsRecordConsumed diskStatsRecordConsumed
    @Inject
    private MemoryStatsRecordConsumed memoryStatsRecordConsumed
    @Inject
    private SwapStatsRecordConsumed swapStatsRecordConsumed
    @Inject
    private SystemStatsRecordConsumed systemStatsRecordConsumed
    @Inject
    private TasksRecordConsumed tasksRecordConsumed
    @Inject
    private ProcessIoStatsRecordConsumed processIoStatsRecordConsumed
    @Inject
    private ProcessStatsRecordConsumed processStatsRecordConsumed

    @Inject
    ConfiguredStreamBuilder builder

    @Bean
    @Singleton
    @Named(SystemStatsRecord.topic)
    KStream<String, SystemStatsRecord> systemStatsRecordsStream() {
        builder.stream(SystemStatsRecord.topic, systemStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(TasksRecord.topic)
    KStream<String, TasksRecord> tasksRecordStream() {
        builder.stream(TasksRecord.topic, tasksRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(SwapStatsRecord.topic)
    KStream<String, SwapStatsRecord> swapStatsRecordStream() {
        builder.stream(SwapStatsRecord.topic, swapStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(MemoryStatsRecord.topic)
    KStream<String, MemoryStatsRecord> memoryStatsRecordStream() {
        builder.stream(MemoryStatsRecord.topic, memoryStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(DiskStatsRecord.topic)
    KStream<String, DiskStatsRecord> diskStatsRecordStream() {
        builder.stream(DiskStatsRecord.topic, diskStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(CpuStatsRecord.topic)
    KStream<String, CpuStatsRecord> cpuStatsRecordStream() {
        builder.stream(CpuStatsRecord.topic, cpuStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(ProcessStatsRecord.topic)
    KStream<String, ProcessStatsRecord> processStatsRecordStream() {
        builder.stream(ProcessStatsRecord.topic, processStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(ProcessIoStatsRecord.topic)
    KStream<String, ProcessIoStatsRecord> processIoStatsRecordStream() {
        builder.stream(ProcessIoStatsRecord.topic, processIoStatsRecordConsumed)
    }

    @Bean
    @Singleton
    @Named(ComputerRecord.topic)
    GlobalKTable<String, ComputerRecord> computerRecordTable() {
        builder.globalTable(ComputerRecord.topic, computerRecordConsumed)
    }
}
