package ti.lsm.henrique.io.process

import io.micronaut.context.annotation.Replaces
import io.reactivex.Flowable
import javax.inject.Singleton

@Replaces(ProcessExecutorImp::class)
@Singleton
class MockProcessExecutor : ProcessExecutor {

    lateinit var flowable: Flowable<String>


    override fun start(vararg command: String): Flowable<String> {
        return flowable
    }

    override fun isStoped(): Boolean {
        return false
    }

    override fun close() {

    }
}