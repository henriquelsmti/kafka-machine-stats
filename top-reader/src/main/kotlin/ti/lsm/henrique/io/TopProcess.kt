package ti.lsm.henrique.io

import io.reactivex.Flowable
import org.apache.logging.log4j.kotlin.logger
import ti.lsm.henrique.Application
import ti.lsm.henrique.io.linereader.LineReader
import ti.lsm.henrique.io.linereader.TopLineReader
import ti.lsm.henrique.io.linereader.TopReader
import ti.lsm.henrique.model.KafkaRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopProcess {

    @Inject
    lateinit var executor: ProcessExecutor

    private val log = logger()

    private val lineReaders = mapOf<Regex, LineReader<*>>(
            TopLineReader.regex to TopLineReader()
    )

    fun init(): Flowable<KafkaRecord> {
        Application.context.getBeansOfType(TopReader::class.java)

        val stream = executor.start("top", "-b").map { line ->
            val key = lineReaders.keys.find { regex ->
                regex.matches(line)
            }
            line to key
        }

        stream.filter {
            it.second == null
        }.subscribe {
            log.warn("line is not readable" to it.first)
        }

        return stream.filter {
            it.second != null
        }.map {
            lineReaders[it.second]!!.read(it.first)
        }
    }

}