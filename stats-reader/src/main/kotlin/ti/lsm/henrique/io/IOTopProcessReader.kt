package ti.lsm.henrique.io

import io.reactivex.Flowable
import ti.lsm.henrique.io.linereader.IOTopReader
import ti.lsm.henrique.model.KafkaRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IOTopProcessReader : ProcessReader {

    @Inject
    lateinit var lineReaders: List<IOTopReader<*>>

    @Inject
    lateinit var processFlowable:ProcessFlowable

    private val linesToIgnore: List<Regex> = listOf(
            Regex("\\s*PID\\s+PRIO\\s+USER\\s+DISK\\s+READ\\s+DISK\\s+WRITE\\s+SWAPIN\\s+IO>\\s+COMMAND\\s*"),
            Regex("Current\\s+DISK\\s+READ:.*")
    )

    override fun init(): Flowable<KafkaRecord> {
        return processFlowable.init(listOf("sudo", "iotop", "-b", "--processes", "--kilobytes"), lineReaders, linesToIgnore)
    }
}