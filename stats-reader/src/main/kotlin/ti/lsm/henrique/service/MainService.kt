package ti.lsm.henrique.service

import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(MainService::class.java)

    private var messageBySecond: Long = 0

    @Scheduled(fixedRate = "1s")
    internal fun logMs() {
        log.info("M/s: $messageBySecond")
        messageBySecond = 0
    }

    @PostConstruct
    fun init() {
        processReaders.forEach { processReader ->
            processReader.init().subscribe {
                kafkaClient.sendRecord(it.topic, it.key, it)
                messageBySecond++
            }
        }
    }
}