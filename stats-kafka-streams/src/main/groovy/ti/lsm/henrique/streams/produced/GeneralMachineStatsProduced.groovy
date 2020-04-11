package ti.lsm.henrique.streams.produced

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.context.annotation.Prototype
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Produced
import ti.lsm.henrique.streams.model.GeneralMachineStats

import javax.inject.Inject

@Prototype
@CompileStatic
class GeneralMachineStatsProduced extends Produced<String, GeneralMachineStats> {

    @Inject
    protected GeneralMachineStatsProduced(JacksonObjectSerializer objectSerializer) {
        super(with(Serdes.String(), new JsonSerde<GeneralMachineStats>(objectSerializer, GeneralMachineStats)))
    }
}
