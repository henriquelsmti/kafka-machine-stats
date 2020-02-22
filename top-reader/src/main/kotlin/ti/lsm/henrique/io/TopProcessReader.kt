package ti.lsm.henrique.io

import io.reactivex.Flowable
import org.apache.logging.log4j.kotlin.logger
import ti.lsm.henrique.io.linereader.TopReader
import ti.lsm.henrique.model.KafkaRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopProcessReader : ProcessReader {

    @Inject
    lateinit var lineReaders: List<TopReader<*>>

    @Inject
    lateinit var processFlowable:ProcessFlowable

    private val linesToIgnore: List<Regex> = listOf(
            Regex("\\s*PID\\s+USER\\s+PR\\s+NI\\s+VIRT\\s+RES\\s+SHR\\s+S\\s+%CPU\\s+%MEM\\s+TIME\\+\\s+COMMAND\\s*")
    )

    override fun init(): Flowable<KafkaRecord> {
        return processFlowable.init(listOf("top", "-b"), lineReaders, linesToIgnore)
    }

}