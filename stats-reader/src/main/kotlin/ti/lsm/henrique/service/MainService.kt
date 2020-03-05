package ti.lsm.henrique.service

import io.micronaut.context.annotation.Context
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
    private var lastLog: Long = System.currentTimeMillis()


    @PostConstruct
    fun init() {
        processReaders.forEach { processReader ->
            processReader.init().subscribe {
                kafkaClient.sendRecord(it.topic, it.key, it)
                messageBySecond++
                if (System.currentTimeMillis() - lastLog >= 1000) {
                    lastLog = System.currentTimeMillis()
                    log.info("M/s: $messageBySecond")
                    messageBySecond = 0
                }
            }
        }
    }
}