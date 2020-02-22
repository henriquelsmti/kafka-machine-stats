package ti.lsm.henrique.io.process

import io.micronaut.context.annotation.Prototype
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import org.apache.logging.log4j.kotlin.logger
import ti.lsm.henrique.io.exceptions.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Executors
import javax.annotation.PreDestroy


@Prototype
class ProcessExecutorImp : ProcessExecutor {

    private lateinit var process: Process
    private var closed = false
    private lateinit var inputStream: BufferedReader
    private val log = logger()
    private val pool = Executors.newFixedThreadPool(1)
    private var started: Boolean = false

    @PreDestroy
    override fun close() {
        closed = true
        pool.shutdown()
        inputStream.close()
        process.destroy()
    }

    override fun isStoped(): Boolean {
        return !process.isAlive
    }


    override fun start(vararg command: String): Flowable<String> {

        if (started) {
            throw IOException("ProcessExecutor started!")
        }

        started = true

        val observable = PublishSubject.create<String>()
        val pocessBuilder = ProcessBuilder(command.asList())
        process = pocessBuilder.start()
        inputStream = BufferedReader(InputStreamReader(process.inputStream))
        pool.submit {
            try {
                while (true) {
                    val line = inputStream.readLine()
                    if (line != null) {
                        observable.onNext(line)
                    }
                }
            } catch (e: Exception) {
                log.error(e.message ?: "Null", e)
                throw e
            }
        }
        return observable.toFlowable(BackpressureStrategy.LATEST)
    }
}