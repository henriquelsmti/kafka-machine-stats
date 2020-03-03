package ti.lsm.henrique.io.process

import io.reactivex.subjects.PublishSubject
import org.apache.logging.log4j.kotlin.logger
import java.io.BufferedReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.annotation.PreDestroy
import javax.inject.Singleton

@Singleton
class LineEmitter {

    private val readersAndPublishers = ConcurrentHashMap<BufferedReader, PublishSubject<String>>()

    private val pool = Executors.newFixedThreadPool(1)
    private val log = logger()
    private var future: Future<*>? = null


    fun registre(reader: BufferedReader, publisher: PublishSubject<String>) {
        readersAndPublishers[reader] = publisher
        checkFuture()
    }

    fun unregister(reader: BufferedReader) {
        readersAndPublishers.remove(reader)
    }

    fun getReadersAndFlowables(): Map<BufferedReader, PublishSubject<String>> {
        return readersAndPublishers.toMap()
    }

    @PreDestroy
    private fun preDestroy() {
        pool.shutdown()
    }

    private fun checkFuture() {
        if (future == null) {
            initTask()
        }
    }

    private fun initTask() {
        future = pool.submit {
            while (true) {
                try {
                    readersAndPublishers.forEach { (reader: BufferedReader, publisher: PublishSubject<String>) ->
                        val line = reader.readLine()
                        if (line != null) {
                            publisher.onNext(line)
                        }
                    }
                } catch (e: Exception) {
                    log.error(e.message ?: "Null", e)
                }
            }
        }
    }
}