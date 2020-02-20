package ti.lsm.henrique.io.process

import io.reactivex.Flowable
import java.io.Closeable

interface ProcessExecutor : Closeable {

    fun start(vararg command: String): Flowable<String>
    fun isStoped(): Boolean
}