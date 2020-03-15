package ti.lsm.henrique.streams.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import ti.lsm.henrique.model.ComputerRecord
import ti.lsm.henrique.model.ProcessIoStatsRecord
import ti.lsm.henrique.model.ProcessStatsRecord
import io.micronaut.core.annotation.Introspected

@Introspected
@CompileStatic
@Immutable(knownImmutableClasses = [ProcessIoStatsRecord, ProcessStatsRecord, ComputerRecord])
class ProcessResourcesUsage {
    ProcessIoStatsRecord processIoStatsRecord
    ProcessStatsRecord processStatsRecord
    ComputerRecord computerRecord
}
