package ti.lsm.henrique.service

import io.micronaut.context.annotation.Context
import ti.lsm.henrique.io.ProcessReader
import ti.lsm.henrique.kafka.KafkaClient
import javax.annotation.PostConstruct
import javax.inject.Inject

@Context
class MainService {

    @Inject
    lateinit var kafkaClient: KafkaClient
    @Inject
    lateinit var processReaders: List<ProcessReader>

    @PostConstruct
    fun init() {
        processReaders.forEach { processReader ->
            processReader.init().subscribe {
                kafkaClient.sendRecord(it.topic, it.key, it)
            }
        }
    }
}