package ti.lsm.henrique.kafka

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import ti.lsm.henrique.model.KafkaRecord
import java.util.concurrent.Future


@KafkaClient
interface   KafkaClient {
    fun sendRecord(@Topic topic:String, @KafkaKey key:String, name:KafkaRecord):Future<KafkaRecord>
}