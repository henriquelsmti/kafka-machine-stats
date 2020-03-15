package ti.lsm.henrique.streams.model

import groovy.transform.CompileStatic
import ti.lsm.henrique.model.*
import groovy.transform.Immutable
import io.micronaut.core.annotation.Introspected

@Introspected
@CompileStatic
@Immutable(knownImmutableClasses = [
        ComputerRecord,
        SystemStatsRecord,
        DiskStatsRecord,
        CpuStatsRecord,
        MemoryStatsRecord,
        SwapStatsRecord,
        TasksRecord
])
class GeneralMachineStats {

    ComputerRecord computerRecord
    DiskStatsRecord diskStatsRecord
    CpuStatsRecord cpuStatsRecord
    MemoryStatsRecord memoryStatsRecord
    SwapStatsRecord swapStatsRecord
    SystemStatsRecord systemStatsRecord
    TasksRecord tasksRecord
}
