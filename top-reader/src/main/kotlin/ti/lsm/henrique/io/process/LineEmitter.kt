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

    private val readersAndFlowables = ConcurrentHashMap<BufferedReader, PublishSubject<String>>()

    private val pool = Executors.newFixedThreadPool(1)
    private val log = logger()
    private var future: Future<*>? = null


    fun registre(reader: BufferedReader, observable: PublishSubject<String>) {
        readersAndFlowables[reader] = observable
        checkFuture()
    }

    fun unregister(reader: BufferedReader) {
        readersAndFlowables.remove(reader)
    }

    fun getReadersAndFlowables():Map<BufferedReader, PublishSubject<String>>{
        return readersAndFlowables.toMap()
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
                    readersAndFlowables.forEach { (reader: BufferedReader, observable: PublishSubject<String>) ->
                        val line = reader.readLine()
                        if (line != null) {
                            observable.onNext(line)
                        }
                    }
                } catch (e: Exception) {
                    log.error(e.message ?: "Null", e)
                }
            }
        }
    }


}