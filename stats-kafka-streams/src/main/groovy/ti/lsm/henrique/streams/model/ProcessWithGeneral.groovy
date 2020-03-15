package ti.lsm.henrique.streams.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import io.micronaut.core.annotation.Introspected

@Immutable
@Introspected
@CompileStatic
class ProcessWithGeneral {
    GeneralMachineStats generalMachineStats
    ProcessResourcesUsage processResourcesUsage
}
