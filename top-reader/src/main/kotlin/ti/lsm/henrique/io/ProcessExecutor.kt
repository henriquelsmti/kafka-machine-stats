package ti.lsm.henrique.io

import io.micronaut.context.annotation.Prototype
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import org.apache.logging.log4j.kotlin.logger
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.util.concurrent.Executors


@Prototype
class ProcessExecutor : Closeable {

    lateinit var inputStream: BufferedReader
    lateinit var process: Process
    private var closed = false
    private val log = logger()
    private val pool = Executors.newFixedThreadPool(1)

    override fun close() {
        closed = true
        inputStream.close()
        process.destroy()
        pool.shutdown()
    }

    fun isStoped(): Boolean {
        return !process.isAlive
    }


    fun start(vararg command: String): Flowable<String> {

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