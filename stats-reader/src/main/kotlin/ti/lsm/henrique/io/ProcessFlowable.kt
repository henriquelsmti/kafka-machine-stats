package ti.lsm.henrique.io

import io.micronaut.context.annotation.Prototype
import io.reactivex.Flowable
import org.slf4j.LoggerFactory
import ti.lsm.henrique.io.linereader.LineReader
import ti.lsm.henrique.io.process.ProcessExecutor
import ti.lsm.henrique.model.KafkaRecord
import ti.lsm.henrique.model.LostRecord
import javax.inject.Inject

@Prototype
class ProcessFlowable {

    private val log = LoggerFactory.getLogger(ProcessFlowable::class.java)

    @Inject
    lateinit var computerIdentifier: ComputerIdentifier

    @Inject
    lateinit var executor: ProcessExecutor

    fun init(command: List<String>, lineReaders: List<LineReader<*>>, linesToIgnore: List<Regex>): Flowable<KafkaRecord> {

        val lineReadersMap = lineReaders.associateBy { it.regex }

        val stream = executor.start(command)
                .filter { line ->
                    line.isNotBlank() && linesToIgnore.none { it.matches(line) }
                }
                .map { line ->
                    val key = lineReadersMap.keys.find { regex ->
                        regex.matches(line)
                    }
                    line to key
                }

        val lost = stream.filter {
            it.second == null
        }.map {
            log.warn("line is not readable to ${it.first}")
            LostRecord(computerIdentifier.id, it.first)
        }

        return stream.filter {
            it.second != null
        }.map {
            lineReadersMap[it.second]!!.read(it.first)
        }.mergeWith(lost)
    }
}