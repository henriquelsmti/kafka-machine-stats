package ti.lsm.henrique.streams.consumeds

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.context.annotation.Prototype
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import ti.lsm.henrique.model.ComputerRecord

import javax.inject.Inject

@Prototype
@CompileStatic
class ComputerRecordConsumed extends Consumed<String, ComputerRecord> {

    @Inject
    protected ComputerRecordConsumed(JacksonObjectSerializer objectSerializer) {
        super(with(Serdes.String(), new JsonSerde<ComputerRecord>(objectSerializer, ComputerRecord)))
    }
}
