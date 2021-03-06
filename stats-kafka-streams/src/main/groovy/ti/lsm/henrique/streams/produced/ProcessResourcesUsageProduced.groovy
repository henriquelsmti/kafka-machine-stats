package ti.lsm.henrique.streams.produced

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.context.annotation.Prototype
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Produced
import ti.lsm.henrique.streams.model.ProcessResourcesUsage

import javax.inject.Inject

@Prototype
@CompileStatic
class ProcessResourcesUsageProduced extends Produced<String, ProcessResourcesUsage> {

    @Inject
    protected ProcessResourcesUsageProduced(JacksonObjectSerializer objectSerializer) {
        super(with(Serdes.String(), new JsonSerde<ProcessResourcesUsage>(objectSerializer, ProcessResourcesUsage)))
    }
}
