package ti.lsm.henrique.io

import io.reactivex.Flowable
import org.apache.logging.log4j.kotlin.logger
import ti.lsm.henrique.io.linereader.TopReader
import ti.lsm.henrique.io.process.ProcessExecutor
import ti.lsm.henrique.model.KafkaRecord
import ti.lsm.henrique.model.LostRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopProcessReader : ProcessReader {

    @Inject
    lateinit var computerIdentifier: ComputerIdentifier

    @Inject
    lateinit var executor: ProcessExecutor

    @Inject
    lateinit var lineReaders: List<TopReader<*>>

    private val log = logger()

    private val linesToIgnore: List<Regex> = listOf(
            Regex("\\s*PID\\s+USER\\s+PR\\s+NI\\s+VIRT\\s+RES\\s+SHR\\s+S\\s+%CPU\\s+%MEM\\s+TIME\\+\\s+COMMAND")
    )

    override fun init(): Flowable<KafkaRecord> {

        val lineReaders = this.lineReaders.associate {
            it.regex to it
        }

        val stream = executor.start("top", "-b")
                .filter { line ->
                    linesToIgnore.none { it.matches(line) }
                }
                .map { line ->
                    val key = lineReaders.keys.find { regex ->
                        regex.matches(line)
                    }
                    line to key
                }

        val lost = stream.filter {
            it.second == null
        }.map {
            log.warn("line is not readable" to it.first)
            LostRecord(computerIdentifier.id, it.first)
        }

        return stream.filter {
            it.second != null
        }.map {
            lineReaders[it.second]!!.read(it.first)
        }.mergeWith(lost)
    }

}