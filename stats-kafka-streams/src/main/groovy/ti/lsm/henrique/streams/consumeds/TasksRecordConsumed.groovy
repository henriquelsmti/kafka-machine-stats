package ti.lsm.henrique.streams.consumeds

import groovy.transform.CompileStatic
import io.micronaut.configuration.kafka.serde.JsonSerde
import io.micronaut.context.annotation.Prototype
import io.micronaut.jackson.serialize.JacksonObjectSerializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import ti.lsm.henrique.model.TasksRecord

import javax.inject.Inject

@Prototype
@CompileStatic
class TasksRecordConsumed extends Consumed<String, TasksRecord> {

    @Inject
    protected TasksRecordConsumed(JacksonObjectSerializer objectSerializer) {
        super(with(Serdes.String(), new JsonSerde<TasksRecord>(objectSerializer, TasksRecord)))
    }
}
