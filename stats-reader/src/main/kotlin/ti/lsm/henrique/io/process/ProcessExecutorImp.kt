package ti.lsm.henrique.io.process

import io.micronaut.context.annotation.Prototype
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import ti.lsm.henrique.io.exceptions.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.annotation.PreDestroy
import javax.inject.Inject


@Prototype
class ProcessExecutorImp : ProcessExecutor {

    @Inject
    lateinit var lineEmitter:LineEmitter

    private var closed = false
    private lateinit var process: Process
    private lateinit var reader: BufferedReader
    private val log = LoggerFactory.getLogger(ProcessExecutorImp::class.java)
    private var started: Boolean = false
    private var command: String = ""

    @PreDestroy
    override fun close() {
        closed = true
        lineEmitter.unregister(reader)
        reader.close()
        process.destroy()
        log.info("Process $command stoped with success.")
    }

    override fun isStoped(): Boolean {
        return !process.isAlive
    }


    override fun start(command: List<String>): Flowable<String> {
        if (started) {
            throw IOException("ProcessExecutor started!")
        }
        this.command = command.joinToString(separator = " ")
        started = true

        val publish = PublishSubject.create<String>()
        val pocessBuilder = ProcessBuilder(command)
        pocessBuilder.environment()["LC_ALL"] = "C"
        process = pocessBuilder.start()
        reader = BufferedReader(InputStreamReader(process.inputStream))
        lineEmitter.registre(reader, publish)
        return publish.toFlowable(BackpressureStrategy.LATEST)
    }
}